package com.letzgo.LetzgoBe.domain.map.apiClient;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class NaverApiClient {

    private final WebClient webClient;

    public NaverApiClient(@Value("${naver.client-id}") String clientId,
                          @Value("${naver.client-secret}") String clientSecret
                          ) {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();
    }

    //좌표를 파라미터로 받아 주소명으로 변환
    public String doReverseGeocoding(long latitude, long longitude) {
        return webClient.get()
                .uri("")
                .retrieve()
                .bodyToMono(String.class)
                .block();  // 동기 방식 (비동기 방식 사용 가능)
    }
}
