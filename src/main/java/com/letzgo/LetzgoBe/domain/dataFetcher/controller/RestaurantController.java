package com.letzgo.LetzgoBe.domain.dataFetcher.controller;

import com.letzgo.LetzgoBe.domain.dataFetcher.service.GeocodingService;
import com.letzgo.LetzgoBe.domain.dataFetcher.service.InfoProvideService;
import com.letzgo.LetzgoBe.domain.dataFetcher.service.RestaurantInfoService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurant")
public class RestaurantController {
    private final RestaurantInfoService restaurantInfoService;
    private final InfoProvideService infoProvideService;
    private final GeocodingService geocodingService;

    //해당하는 지역의 식당정보들을 반환
    //region: "경기도", "제주특별자치도", "충청남도", "인천광역시", "대구광역시", "대전광역시", "서울특별시", "경상남도", "부산광역시", "전북특별자치도",
    //"울산광역시", "광주광역시", "강원특별자치도", "경상북도", "전라남도", "충청북도", "세종특별자치시"
    @GetMapping("/info")
    ApiResponse getRestaurantsInfo(@RequestParam("region") String region) {
        return ApiResponse.of(infoProvideService.getRestaurantInfo(region));
    }

    //수동실행
    @GetMapping("/test/{scroll}")
    void test(@PathVariable int scroll) {
        restaurantInfoService.getRestaurantsInfo(scroll);
    }
    @GetMapping("/testRegion/{region}")
    void testRegion(@PathVariable("region") String region) {
        restaurantInfoService.getRegionRestaurantsInfo(100, region);
    }
    @GetMapping("/testGeo")
    void testGeo() {
        geocodingService.updateRestaurantCoordinates();
    }
}
