package com.letzgo.LetzgoBe.domain.dataFetcher.scheduler;

import com.letzgo.LetzgoBe.domain.dataFetcher.service.GeocodingService;
import com.letzgo.LetzgoBe.domain.dataFetcher.service.HotelInfoService;
import com.letzgo.LetzgoBe.domain.dataFetcher.service.RestaurantInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataFetchSchedule {
    private final HotelInfoService hotelInfoService;
    private final RestaurantInfoService restaurantInfoService;
    private final GeocodingService geocodingService;

    @Value("${schedule.use}")
    private boolean useSchedule;
    @Value("${schedule.hotel.use-page}")
    //지역당 가져올 페이지 수, 페이지당 숙소 20개
    private int page;
    @Value("${schedule.restaurant.use-scroll}")
    //지역페이지에서 스크롤할 횟수
    private int scroll;

//    @Scheduled(cron = "${schedule.hotel.cron}")
    public void fetchHotelDataSchedule() {
        try {
            if (useSchedule)
                hotelInfoService.getHotelsInfo(page);
        } catch (Exception e) {
            log.warn("hotelInfoService 스케줄링 중 오류 발생");
        }
    }

//    @Scheduled(cron = "${schedule.restaurant.cron}")
    public void fetchRestaurantDataSchedule() {
        try {
            if (useSchedule)
                restaurantInfoService.getRestaurantsInfo(scroll);
        } catch (Exception e) {
            log.warn("restaurantInfoService 스케줄링 중 오류 발생");
        }
    }

//    @Scheduled(cron = "${schedule.geocoding.hotel}")
    public void addHotelCoordinateSchedule() {
        try {
            if (useSchedule)
                geocodingService.updateHotelCoordinates();
        } catch (Exception e) {
            log.warn("geocodingService(호텔) 스케줄링 중 오류 발생");
        }

    }

//    @Scheduled(cron = "${schedule.geocoding.restaurant}")
    public void addRestaurantCoordinateSchedule() {
        try {
            if (useSchedule)
                geocodingService.updateRestaurantCoordinates();
        } catch (Exception e) {
            log.warn("geocodingService(식당) 스케줄링 중 오류 발생");
        }
    }
}
