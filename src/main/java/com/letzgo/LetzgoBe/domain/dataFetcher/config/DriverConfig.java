package com.letzgo.LetzgoBe.domain.dataFetcher.config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DriverConfig {
    @Value("${selenium.driver-path}")
    private String webDriverPath; //프로젝트 폴터에 크롬드라이버 필요, 컴퓨터마다 경로 다름

    @Bean(destroyMethod = "close")
    public WebDriver Driver() {
        String WEB_DRIVER_ID = "webdriver.chrome.driver";
        System.setProperty(WEB_DRIVER_ID, webDriverPath);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
        //options.addArguments("--headless"); //주석처리시 브라우저 보임

        return new ChromeDriver(options);
    }
}
