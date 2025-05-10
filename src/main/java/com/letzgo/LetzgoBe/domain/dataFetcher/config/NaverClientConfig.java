package com.letzgo.LetzgoBe.domain.dataFetcher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class NaverClientConfig {
    private static final String BASE_URL = "https://maps.apigw.ntruss.com/map-geocode/v2/geocode";

    @Value("${NAVER_CLOUDPLATFROM_ID}")
    private String clientId;

    @Value("${NAVER_CLOUDPLATFROM_SECRET}")
    private String clientSecret;


    @Bean
    public WebClient naverApiClient() {
        return WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("x-ncp-apigw-api-key-id", clientId)
                .defaultHeader("x-ncp-apigw-api-key", clientSecret)
                .build();
    }
}
