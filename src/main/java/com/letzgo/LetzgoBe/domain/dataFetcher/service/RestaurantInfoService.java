package com.letzgo.LetzgoBe.domain.dataFetcher.service;

import com.letzgo.LetzgoBe.domain.dataFetcher.entity.Restaurant;
import com.letzgo.LetzgoBe.domain.dataFetcher.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantInfoService {
    private final WebDriver driver;
    private final RestaurantRepository restaurantRepository;

    public void getRestaurantsInfo() {

        String[] rigions = {"서울", "강원", "경기", "경남", "경북", "광주", "대구", "대전", "부산", "세종", "울산"};
        for (String rigion : rigions) {
            getListPageInfo("https://www.diningcode.com/?region=" + rigion);
        }
    }

    private void getListPageInfo(String ListPageUrl) {
        try {
            driver.get(ListPageUrl);

            JavascriptExecutor js = (JavascriptExecutor) driver;

            long lastHeight = (long) js.executeScript("return document.body.scrollHeight");

            while (true) {
                // 스크롤을 최하단으로 내리기
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                Thread.sleep(1000);

                long newHeight = (long) js.executeScript("return document.body.scrollHeight");
                if (newHeight == lastHeight) {
                    break;
                }
                lastHeight = newHeight;
            }

            //음식 테마별 "자세히 보기" 링크들
            List<WebElement> links = driver.findElements(By.xpath("//a[starts-with(@href, '/recom_detail')]"));
            getDetailPageInfo(links);

        } catch (Exception e) {
        }

    }

    private void getDetailPageInfo(List<WebElement> links) {
        String originalWindow = driver.getWindowHandle();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        for (WebElement link : links) {
            // 새 창에서 링크 클릭
            String openInNewTab = Keys.chord(Keys.CONTROL, Keys.ENTER);
            link.sendKeys(openInNewTab);

            // 새 탭으로 전환
            for (String tab : driver.getWindowHandles()) {
                driver.switchTo().window(tab);
            }

            //대기
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a.PoiBlock")));

            //음식점 상세페이지 링크
            List<String> detailPageUrls = new ArrayList<>();
            List<WebElement> restaurants = driver.findElements(By.cssSelector("a.PoiBlock"));
            for (WebElement restaurant : restaurants) {
                String detailPage = restaurant.getAttribute("data-rid");
                detailPageUrls.add("https://www.diningcode.com/profile.php?rid=" + detailPage);
            }


            for (String urls : detailPageUrls) {
                driver.get(urls);

                //이름
                String title = driver.findElement(By.cssSelector("#div_profile > div.s-list.pic-grade > div.tit-point > h1")).getText();
                //평점
                String rating = driver.findElement(By.cssSelector("#lbl_review_point")).getText();

                //주소
                WebElement locationElement = driver.findElement(By.className("locat"));
                List<WebElement> roadAddressElements = locationElement.findElements(By.tagName("a"));

                StringBuilder _roadAddress = new StringBuilder();
                for (WebElement element : roadAddressElements) {
                    _roadAddress.append(element.getText()).append(" ");
                }
                WebElement floorElement = locationElement.findElement(By.tagName("span"));
                _roadAddress.append(floorElement.getText().trim());

                String roadAddress = _roadAddress.toString().trim();

                //음식카테고리
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
                        .location(roadAddress)
                        .rating(Float.parseFloat(rating))
                        .category(category)
                        .imagePath(imagePath)
                        .build();

                restaurantRepository.save(restaurant);
            }

            driver.close();
            driver.switchTo().window(originalWindow);
        }
    }
}
