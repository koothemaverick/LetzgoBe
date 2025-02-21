package com.letzgo.LetzgoBe.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

//사용예시:map.service.MapApiService

@Configuration
public class WebClientConfig {
    //네이버 클라우드플랫폼Api (지도관련기능 등)
    @Bean
    public WebClient naverCloudPlatformApiClient(@Value("${api-keys.naver.cloud-platform.client-id}") String clientId,
                                    @Value("${api-keys.naver.cloud-platform.client-secret}") String clientSecret
                                    ) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://naveropenapi.apigw.ntruss.com")
                .defaultHeader("x-ncp-apigw-api-key-id", clientId)
                .defaultHeader("x-ncp-apigw-api-key", clientSecret)
                .build();

        return webClient;
    }

    //네이버 openApi (검색기능 등)
    @Bean
    public WebClient naverOpenApiClient(@Value("${api-keys.naver.open-api.client-id}") String clientId,
                                        @Value("${api-keys.naver.open-api.client-secret}") String clientSecret
    ) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://openapi.naver.com")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();

        return webClient;
    }


    @Bean
    public WebClient googleApiClient(@Value("${api-keys.google.client-key}") String clientKey) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://places.googleapis.com")
                .defaultHeader("X-Goog-Api-Key", clientKey)
                .build();

        return webClient;
    }

}
