package com.letzgo.LetzgoBe.domain.schedule.service;

import com.letzgo.LetzgoBe.domain.map.entity.Place;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Google Directions API를 이용해 장소 간 최적 경유지 순서를 계산하는 유틸 클래스
 */
@Component
@RequiredArgsConstructor
public class GoogleDirectionsClient {

    private final RestTemplate restTemplate = new RestTemplate();

//    @Value("${google.maps.api.key}")
    private String apiKey;

    /**
     * 장소 리스트를 받아서 최적 방문 순서 인덱스를 반환
     */
    public List<Integer> optimizeWaypointOrder(List<Place> places) {
        if (places.size() < 2) return List.of();

//        String origin = places.get(0).getLatitude() + "," + places.get(0).getLongitude();
//        String destination = places.get(places.size() - 1).getLatitude() + "," + places.get(places.size() - 1).getLongitude();
        String origin = "";
        String destination = "";

        List<String> waypoints = new ArrayList<>();
//        for (int i = 1; i < places.size() - 1; i++) {
//            Place p = places.get(i);
//            waypoints.add(p.getLatitude() + "," + p.getLongitude());
//        }

        String waypointParam = "optimize:true" + (waypoints.isEmpty() ? "" : "|" + String.join("|", waypoints));

        String url = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/directions/json")
                .queryParam("origin", origin)
                .queryParam("destination", destination)
                .queryParam("waypoints", waypointParam)
                .queryParam("key", apiKey)
                .toUriString();

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response != null && response.containsKey("routes")) {
            List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
            if (!routes.isEmpty()) {
                Map<String, Object> route = routes.get(0);
                List<Integer> waypointOrder = (List<Integer>) route.get("waypoint_order");

                // 반환된 순서는 waypoints의 순서를 기준으로 함 → 중간 위치만 정렬됨
                List<Integer> fullOrder = new ArrayList<>();
                fullOrder.add(0); // 시작
                for (int idx : waypointOrder) fullOrder.add(idx + 1); // 중간
                fullOrder.add(places.size() - 1); // 도착
                return fullOrder;
            }
        }

        return List.of(); // fallback
    }
}
