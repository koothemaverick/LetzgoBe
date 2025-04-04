package com.letzgo.LetzgoBe.domain.dataFetcher.service;

import com.letzgo.LetzgoBe.domain.dataFetcher.dto.HotelDto;
import com.letzgo.LetzgoBe.domain.dataFetcher.entity.Hotel;
import com.letzgo.LetzgoBe.domain.dataFetcher.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class HotelInfoService {
    private final WebDriver driver;
    private final HotelRepository hotelRepository;

        public void getHotelsInfo(int page) {
        String[] rigions = {"경기도", "제주특별자치도", "충청남도", "인천광역시", "대구광역시", "대전광역시", "서울특별시", "경상남도", "부산광역시", "전북특별자치도",
                "울산광역시", "광주광역시", "강원특별자치도", "경상북도", "전라남도", "충청북도", "세종특별자치시"};
        for (String rigion : rigions) {
            for (int i = 1; i <= page; i++) {
                if (i == 1)
                    getListPageInfo("https://www.yeogi.com/domestic-accommodations?keyword=" + rigion + "&category=0&freeForm=true");
                else
                    getListPageInfo("https://www.yeogi.com/domestic-accommodations?keyword=" + rigion + "&category=0&freeForm=true" + "&page=" + i);
            }
        }
    }

    private void getListPageInfo(String ListPageUrl) {
        for (int i = 3; i <= 22; i++) {
            try {
                driver.get(ListPageUrl);

                String cssSelector = "#__next > div > main > section > div.css-1qumol3 > a:nth-child(" + i + ")";
                WebElement link = driver.findElement(By.cssSelector(cssSelector));

                //가격 가져옴
                String sukbak = null;
                String daesil = null;
                try {
                    sukbak = driver.findElement(By.cssSelector("#__next > div > main > section > div.css-1qumol3 > a:nth-child(" + i + ") > div.css-gvoll6 > div.css-1by0ap6 > div.css-sg6wi7 > div:nth-child(1) > div > div.css-ukl1fa > div > div > span.css-5r5920")).getText();
                } catch (Exception e) {
                }
                try {
                    daesil = driver.findElement(By.cssSelector("#__next > div > main > section > div.css-1qumol3 > a:nth-child(" + i + ") > div.css-gvoll6 > div.css-1by0ap6 > div.css-sg6wi7 > div:nth-child(2) > div > div.css-ukl1fa > div > div > span.css-5r5920")).getText();
                } catch (Exception e) {
                }

                HotelDto hotelDto = HotelDto.builder()
                        .sukbakPrice(sukbak == null ? null : Integer.parseInt(sukbak.replace(",", "")))
                        .daesilPrice(daesil == null ? null : Integer.parseInt(daesil.replace(",", "")))
                        .build();

                String href = link.getAttribute("href");
                getDetailPageInfo(href, hotelDto);
            }
            catch (Exception e) { //한페이지에 숙소가 20개보다 적을경우
            }
        }
    }
    private void getDetailPageInfo(String DetailPageUrl, HotelDto hotelDto) {
        try {
            // 웹페이지 열기
            driver.get(DetailPageUrl);

            //대기
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("css-1t5t2dt")));

            //위치
            String location = driver.findElement(By.className("css-1t5t2dt")).getText();

            //장소명
            String title = driver.findElement(By.className("css-17we8hh")).getText();
            String _title = title.replace("★당일특가★ ", "");

            //평점
            String rating = driver.findElement(By.className("css-2d2ntr")).getText();

            //대표이미지
            WebElement element = driver.findElement(By.cssSelector("#overview > article > div.css-12lmpk7 > ul > li.css-9hh5jq > div > img"));
            String photo = element.getAttribute("srcset");

            hotelDto.setName(_title);
            hotelDto.setLocation(location);
            hotelDto.setRating(Float.parseFloat(rating));
            hotelDto.setImagePath(photo);

            Hotel hotel = Hotel.builder()
                    .name(hotelDto.getName())
                    .location(hotelDto.getLocation())
                    .daesilPrice(hotelDto.getDaesilPrice())
                    .sukbakPrice((hotelDto.getSukbakPrice()))
                    .rating(hotelDto.getRating())
                    .imagePath(hotelDto.getImagePath())
                    .build();

            hotelRepository.save(hotel);

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}
