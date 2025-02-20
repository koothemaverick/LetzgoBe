package com.letzgo.LetzgoBe.domain.map.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MapApiService {

    @Autowired
    private WebClient naverApiClient;
    @Autowired
    private WebClient googleApiClient;

    public String doReverseGeocoding(String coords) {
        return naverApiClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/map-reversegeocode/v2/gc") // api경로
                        .queryParam("coords", coords) // 파라미터
                        .queryParam("output", "json") // 응답형식
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}

