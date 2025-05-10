package com.letzgo.LetzgoBe.domain.dataFetcher.controller;

import com.letzgo.LetzgoBe.domain.dataFetcher.service.GeocodingService;
import com.letzgo.LetzgoBe.domain.dataFetcher.service.HotelInfoService;
import com.letzgo.LetzgoBe.domain.dataFetcher.service.InfoProvideService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/hotel")
public class HotelController {
    private final HotelInfoService hotelInfoService;
    private final GeocodingService geocodingService;
    private final InfoProvideService infoProvideService;

    //해당하는 지역의 호텔정보들을 반환
    //region: "경기도", "제주특별자치도", "충청남도", "인천광역시", "대구광역시", "대전광역시", "서울특별시", "경상남도", "부산광역시", "전북특별자치도",
    //"울산광역시", "광주광역시", "강원특별자치도", "경상북도", "전라남도", "충청북도", "세종특별자치시"
    @GetMapping("/info")
    ApiResponse getHotelsInfo(@RequestParam("region") String region) {
        return ApiResponse.of(infoProvideService.getHotelInfo(region));
    }

    //테스트용 요청
    @GetMapping("/test")
    void test() {
        hotelInfoService.getHotelsInfo(1);
    }
    @GetMapping("/testGeo")
    void testGeo() {geocodingService.updateHotelCoordinates();}
}
