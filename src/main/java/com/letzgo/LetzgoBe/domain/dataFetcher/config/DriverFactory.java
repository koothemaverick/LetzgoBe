package com.letzgo.LetzgoBe.domain.dataFetcher.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@Component
public class DriverFactory {
    @Value("${selenium.remote-url}")
    private String seleniumRemoteUrl;
    public WebDriver createDriver() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-extensions");
        options.addArguments("--blink-settings=imagesEnabled=false");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/113.0 Safari/537.36");

        return new RemoteWebDriver(new URL(seleniumRemoteUrl), options);
    }

    public WebDriver createDriverWithRetry(int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                return createDriver();
            } catch (Exception e) {
                log.warn("WebDriver 생성 실패 (시도 {}/{}): {}", i + 1, maxRetries, e.getMessage());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }
        throw new RuntimeException("WebDriver 생성 실패");
    }



    //로컬테스트용
//    public WebDriver createDriver() {
//        WebDriverManager.chromedriver().setup(); // 로컬용 드라이버 자동 설치
//
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--start-maximized");
//        options.addArguments("--disable-popup-blocking");
//        options.addArguments("--disable-gpu");
//        options.addArguments("--blink-settings=imagesEnabled=false");
//        //options.addArguments("--headless");
//
//        return new ChromeDriver(options);
//    }
}


