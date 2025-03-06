package com.letzgo.LetzgoBe.domain.map.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class MapApiService {

    @Autowired
    private WebClient naverCloudPlatformApiClient;
    @Autowired
    private WebClient naverOpenApiClient;
    @Autowired
    private WebClient googleApiClient;

    public String doReverseGeocoding(String coords) {
        return naverCloudPlatformApiClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/map-reversegeocode/v2/gc") // api경로
                        .queryParam("coords", coords) // 쿼리파라미터
                        .queryParam("output", "json") // 응답형식
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String OpenApiTest(String query) {
        return naverOpenApiClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/search/local.xml")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
    
    public String googleApiTest(String query) {
        return googleApiClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/places:searchText")
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("textQuery", query, "languageCode", "ko"))//요청본문
                .header("X-Goog-FieldMask", "places.name")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


}

