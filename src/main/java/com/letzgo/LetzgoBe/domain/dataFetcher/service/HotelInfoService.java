package com.letzgo.LetzgoBe.domain.dataFetcher.service;

import com.letzgo.LetzgoBe.domain.dataFetcher.config.DriverFactory;
import com.letzgo.LetzgoBe.domain.dataFetcher.dto.req.HotelRequest;
import com.letzgo.LetzgoBe.domain.dataFetcher.entity.Hotel;
import com.letzgo.LetzgoBe.domain.dataFetcher.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelInfoService {
    private final DriverFactory driverFactory;
    private final HotelRepository hotelRepository;

    @Transactional(readOnly = true)
    public void getHotelsInfo(int page) {
        String[] regions = {
                "경기도", "제주특별자치도", "충청남도", "인천광역시", "대구광역시", "대전광역시", "서울특별시",
                "경상남도", "부산광역시", "전북특별자치도", "울산광역시", "광주광역시", "강원특별자치도",
                "경상북도", "전라남도", "충청북도", "세종특별자치시"
        };
        int progress = 0;

        for (String region : regions) {
            log.info("숙소 정보 현재탐색중: {}, 진행율: {}", region, progress + "/" + regions.length);

            for (int i = 1; i <= page; i++) {
                WebDriver driver = null;
                try {
                    driver = driverFactory.createDriverWithRetry(5);
                    String url = "https://www.yeogi.com/domestic-accommodations?keyword=" + region + "&category=0&freeForm=true";
                    if (i > 1) url += "&page=" + i;

                    getListPageInfo(driver, url, region);
                } catch (Exception e) {
                    log.error("크롤링 중 오류 발생", e);
                } finally {
                    if (driver != null) {
                        try {
                            driver.quit();
                        } catch (Exception e) {
                            log.warn("driver 종료 중 예외", e);
                        }
                    }
                }
            }

            progress++;
        }

        log.info("getHotelsInfo 숙소 정보 탐색완료");
    }

    @Transactional(readOnly = true)
    public void getRegionHotelsInfo(int page, String region) {
        log.info("숙소정보 현재탐색중: {}", region);

        for (int i = 1; i <= page; i++) {
            WebDriver driver = null;
            try {
                driver = driverFactory.createDriverWithRetry(5);
                String url = "https://www.yeogi.com/domestic-accommodations?keyword=" + region + "&category=0&freeForm=true";
                if (i > 1) url += "&page=" + i;

                getListPageInfo(driver, url, region);
            } catch (Exception e) {
                log.error("크롤링 중 오류 발생", e);
            } finally {
                if (driver != null) {
                    try {
                        driver.quit();
                    } catch (Exception e) {
                        log.warn("driver 종료 중 예외", e);
                    }
                }
            }
        }

        log.info("getRegionHotelsInfo 숙소 정보 탐색완료");
    }

    // ----------------- 헬퍼 메서드 -----------------

    private void getListPageInfo(WebDriver driver, String listPageUrl, String region) {
        for (int i = 3; i <= 22; i++) {
            try {
                driver.get(listPageUrl);
                log.info("숙소 페이지 접속 완료: {}", listPageUrl);

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                String cssSelector = "#__next > div > main > section > div.css-1qumol3 > a:nth-child(" + i + ")";
                WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelector)));

                String sukbak = null;
                String daesil = null;

                try {
                    daesil = driver.findElement(By.cssSelector(
                            cssSelector + " > div.css-gvoll6 > div.css-1by0ap6 > div.css-sg6wi7 > div:nth-child(1) > div > div.css-ukl1fa > div > div > span.css-5r5920"
                    )).getText();
                } catch (Exception e) {}

                try {
                    sukbak = driver.findElement(By.cssSelector(
                            cssSelector + " > div.css-gvoll6 > div.css-1by0ap6 > div.css-sg6wi7 > div:nth-child(2) > div > div.css-ukl1fa > div > div > span.css-5r5920"
                    )).getText();
                } catch (Exception e) {}

                HotelRequest hotelRequest = HotelRequest.builder()
                        .region(region)
                        .sukbakPrice(sukbak == null ? null : Integer.parseInt(sukbak.replace(",", "")))
                        .daesilPrice(daesil == null ? null : Integer.parseInt(daesil.replace(",", "")))
                        .build();

                String href = link.getAttribute("href");
                getDetailPageInfo(driver, href, hotelRequest);
            } catch (Exception e) {
                log.error("숙소 마지막 페이지. i={}, url={}", i, listPageUrl, e);
            }
        }
    }

    private void getDetailPageInfo(WebDriver driver, String detailPageUrl, HotelRequest hotelRequest) {
        try {
            driver.get(detailPageUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("css-1t5t2dt")));

            String location = driver.findElement(By.className("css-1t5t2dt")).getText();
            String title = driver.findElement(By.className("css-17we8hh")).getText();
            String _title = title.replace("★당일특가★ ", "");
            String rating = driver.findElement(By.className("css-2d2ntr")).getText();

            WebElement element = driver.findElement(By.cssSelector("#overview > article > div.css-12lmpk7 > ul > li.css-9hh5jq > div > img"));
            String photo = element.getAttribute("srcset");

            hotelRequest.setName(_title);
            hotelRequest.setLocation(location);
            hotelRequest.setRating(Float.parseFloat(rating));
            hotelRequest.setImagePath(photo);

            Hotel hotel = Hotel.builder()
                    .name(hotelRequest.getName())
                    .region(hotelRequest.getRegion())
                    .location(hotelRequest.getLocation())
                    .daesilPrice(hotelRequest.getDaesilPrice())
                    .sukbakPrice(hotelRequest.getSukbakPrice())
                    .rating(hotelRequest.getRating())
                    .imagePath(hotelRequest.getImagePath())
                    .build();

            hotelRepository.save(hotel);
        } catch (Exception e) {
        }
    }
}
