package com.letzgo.LetzgoBe.domain.dataFetcher.service;

import com.letzgo.LetzgoBe.domain.dataFetcher.dto.res.HotelResponse;
import com.letzgo.LetzgoBe.domain.dataFetcher.dto.res.RestaurantResponse;
import com.letzgo.LetzgoBe.domain.dataFetcher.entity.Hotel;
import com.letzgo.LetzgoBe.domain.dataFetcher.entity.Restaurant;
import com.letzgo.LetzgoBe.domain.dataFetcher.repository.HotelRepository;
import com.letzgo.LetzgoBe.domain.dataFetcher.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfoProvideService {
    private final HotelRepository hotelRepository;
    private final RestaurantRepository restaurantRepository;

    String[] regions = {"경기도", "제주특별자치도", "충청남도", "인천광역시", "대구광역시", "대전광역시", "서울특별시", "경상남도", "부산광역시", "전북특별자치도",
            "울산광역시", "광주광역시", "강원특별자치도", "경상북도", "전라남도", "충청북도", "세종특별자치시"};

    public List<HotelResponse> getHotelInfo(String region) {
        if (Arrays.asList(regions).contains(region)) {
            List<HotelResponse> hotelInfos = hotelRepository.findByRegion(region).stream()
                    .map(Hotel::toDto).collect(Collectors.toList());
            return hotelInfos;
        } else {
            log.warn("(호텔)올바르지 않은 지역명입니다");
            return null;
        }
    }

    public List<RestaurantResponse> getRestaurantInfo(String region) {
        if (Arrays.asList(regions).contains(region)) {
            List<RestaurantResponse> restaurantInfos = restaurantRepository.findByRegion(region).stream()
                    .map(Restaurant::toDto).collect(Collectors.toList());
            return restaurantInfos;
        } else {
            log.warn("(식당)올바르지 않은 지역명입니다");
            return null;
        }
    }
}
