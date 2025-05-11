package com.letzgo.LetzgoBe.domain.dataFetcher.service;

import com.letzgo.LetzgoBe.domain.dataFetcher.entity.Restaurant;
import com.letzgo.LetzgoBe.domain.dataFetcher.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantInfoService {
    private final WebDriver driver;
    private final RestaurantRepository restaurantRepository;

    public void getRestaurantsInfo(int scroll) {
        //String[] regions = {"서울"};
        String[] regions = {"서울", "강원", "경기", "경남", "경북", "광주", "대구", "대전", "부산", "세종", "울산"};
        int progress = 0;

        for (String region : regions) {
            log.info("식당정보 현재탐색중: {}, 진행율: {}",region, progress+"/"+regions.length);
            getListPageInfo("https://www.diningcode.com/list.dc?query=" + region, region, scroll);
        }

        log.info("식당 정보 탐색완료");
    }

    private void getListPageInfo(String ListPageUrl, String region, int scroll) {
        try {
            driver.get(ListPageUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            WebElement scrollContainer = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("div[class*='Scroll__List__Section']")
                    )
            );

            int maxScrolls = scroll;
            int scrollAmount = 3000;
            int previousCount = 0;
            int unchangedCount = 0;

            for (int i = 0; i < maxScrolls; i++) {
                List<WebElement> poiList = driver.findElements(By.cssSelector("a.PoiBlock"));
                int currentCount = poiList.size();

                // 스크롤
                js.executeScript("arguments[0].scrollTop += arguments[1];", scrollContainer, scrollAmount);
                Thread.sleep(1500);

                if (currentCount == previousCount) {
                    unchangedCount++;
                    // 2번 이상 연속 변화 없을 경우, search more 버튼 클릭 시도
                    if (unchangedCount >= 2) {
                        List<WebElement> searchMoreButtons = driver.findElements(
                                By.cssSelector("div[class*='SearchMore']")
                        );
                        if (!searchMoreButtons.isEmpty()) {
                            try {
                                WebElement button = searchMoreButtons.get(0);
                                js.executeScript("arguments[0].click();", button);
                                Thread.sleep(1500);
                            } catch (Exception clickErr) {
                            }
                        }
                    }
                    if (unchangedCount >= 4) {
                        break;
                    }
                } else {
                    unchangedCount = 0;
                }
                previousCount = currentCount;
            }

            // 음식점 링크 수집
            List<WebElement> links = scrollContainer.findElements(By.cssSelector("a.PoiBlock"));
            getDetailPageInfo(links, region);

        } catch (Exception e) {
        }
    }


    public void getDetailPageInfo(List<WebElement> links, String region) {
        List<String> urls = new ArrayList<>();
        for (WebElement link : links) {
            String url = link.getAttribute("href");
            if (url != null && url.contains("profile.php?rid=")) {
                urls.add(url);
            }
        }

        //System.out.println("총 음식점 수: " + urls.size());

        String originalWindow = driver.getWindowHandle();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 새 탭 하나 열기
        ((JavascriptExecutor) driver).executeScript("window.open()");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 새 탭으로 전환
        String newTab = null;
        for (String tab : driver.getWindowHandles()) {
            if (!tab.equals(originalWindow)) {
                newTab = tab;
                driver.switchTo().window(newTab);
                break;
            }
        }

        for (String url : urls) {
            driver.get(url);

            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#div_profile h1")));

                String title = driver.findElement(By.cssSelector("#div_profile > div.s-list.pic-grade > div.tit-point > h1")).getText();
                String rating = driver.findElement(By.cssSelector("#lbl_review_point")).getText();

                WebElement locationElement = driver.findElement(By.className("locat"));
                List<WebElement> roadAddressElements = locationElement.findElements(By.tagName("a"));
                StringBuilder roadAddress = new StringBuilder();
                for (WebElement element : roadAddressElements) {
                    roadAddress.append(element.getText()).append(" ");
                }
                WebElement floorElement = locationElement.findElement(By.tagName("span"));
                roadAddress.append(floorElement.getText().trim());

                StringBuilder _category = new StringBuilder();
                WebElement categoryContainer = driver.findElement(By.className("btxt"));

                List<WebElement> categoryElements = categoryContainer.findElements(By.cssSelector("a[class*='category-']"));

                for (WebElement categoryElement : categoryElements) {
                    _category.append(categoryElement.getText()).append(" ");
                }
                String category = _category.toString().trim();

                StringBuilder _imagePath = new StringBuilder();
                List<WebElement> imageElements = driver.findElements(By.cssSelector(".s-list.pic-grade img"));
                for (WebElement imageElement : imageElements) {
                    String imagePath = imageElement.getAttribute("src");
                    if (!imagePath.contains("common"))
                        _imagePath.append(imageElement.getAttribute("src")).append(" ");
                }

                String imagePath = _imagePath.toString().trim();


                Restaurant restaurant = Restaurant.builder()
                        .name(title)
                        .region(region)
                        .location(roadAddress.toString().trim())
                        .rating(Float.parseFloat(rating))
                        .category(category)
                        .imagePath(imagePath)
                        .build();

                restaurantRepository.save(restaurant);

            } catch (Exception e) {
            }
        }
        //원래 창으로 복귀
        driver.close();
        driver.switchTo().window(originalWindow);
    }
}



