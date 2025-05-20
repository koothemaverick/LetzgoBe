package com.letzgo.LetzgoBe.domain.dataFetcher.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class DriverFactory {
    @Value("${selenium.remote-url}")
    private String seleniumRemoteUrl;
    public WebDriver createDriver() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized", "--disable-popup-blocking", "--headless");
        return new RemoteWebDriver(new URL(seleniumRemoteUrl), options);
    }

    //로컬테스트용
//    public WebDriver createDriver() {
//        WebDriverManager.chromedriver().setup(); // 로컬용 드라이버 자동 설치
//
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--start-maximized");
//        options.addArguments("--disable-popup-blocking");
//        //options.addArguments("--headless");
//
//        return new ChromeDriver(options);
//    }
}


