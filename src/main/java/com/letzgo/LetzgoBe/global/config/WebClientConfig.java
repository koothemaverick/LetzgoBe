package com.letzgo.LetzgoBe.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient naverApiClient(@Value("${api-keys.naver.client-id}") String clientId,
                                    @Value("${api-keys.naver.client-secret}") String clientSecret
                                    ) {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://naveropenapi.apigw.ntruss.com")
                .defaultHeader("x-ncp-apigw-api-key-id", clientId)
                .defaultHeader("x-ncp-apigw-api-key", clientSecret)
                .build();

        return webClient;
    }

    @Bean
    public WebClient googleApiClient(@Value("${api-keys.google.client-key}") String clientKey) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://maps.googleapis.com")
                .defaultUriVariables(Map.of("key", clientKey))
                .build();

        return webClient;
    }

}
