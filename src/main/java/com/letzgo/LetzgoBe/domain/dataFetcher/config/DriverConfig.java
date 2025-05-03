package com.letzgo.LetzgoBe.domain.dataFetcher.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Configuration
public class DriverConfig {
    @Value("${selenium.remote-url}")
    private String seleniumRemoteUrl;

    @Bean(destroyMethod = "quit")
    public WebDriver Driver() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
        options.addArguments("--headless"); //주석처리시 브라우저 보임

        return new RemoteWebDriver(new URL(seleniumRemoteUrl), options);
    }
}

//로컬 실행용 코드
//@Configuration
//public class DriverConfig {
//    @Bean
//    public WebDriver Driver() {
//        WebDriverManager.chromedriver().setup();
//
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--start-maximized");
//        options.addArguments("--disable-popup-blocking");
//        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
//        options.addArguments("--headless"); //주석처리시 브라우저 보임
//
//        return new ChromeDriver(options);
//    }
//}
