package com.letzgo.LetzgoBe.domain.dataFetcher.controller;

import com.letzgo.LetzgoBe.domain.dataFetcher.service.GeocodingService;
import com.letzgo.LetzgoBe.domain.dataFetcher.service.InfoProvideService;
import com.letzgo.LetzgoBe.domain.dataFetcher.service.RestaurantInfoService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurant")
public class RestaurantController {
    private final RestaurantInfoService restaurantInfoService;
    private final InfoProvideService infoProvideService;
    private final GeocodingService geocodingService;

    //해당하는 지역의 식당정보들을 반환
    //region: "서울", "강원", "경기", "경남", "경북", "광주", "대구", "대전", "부산", "세종", "울산"
    @GetMapping("/info")
    ApiResponse getRestaurantsInfo(@RequestParam("region") String region) {
        return ApiResponse.of(infoProvideService.getRestaurantInfo(region));
    }

    //테스트용요청
    @GetMapping("/test")
    void test() {
        restaurantInfoService.getRestaurantsInfo(2);
    }
    @GetMapping("/testGeo")
    void testGeo() {
        geocodingService.updateRestaurantCoordinates();
    }
}
