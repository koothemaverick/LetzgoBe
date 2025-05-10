package com.letzgo.LetzgoBe.domain.dataFetcher.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.letzgo.LetzgoBe.domain.dataFetcher.entity.Hotel;
import com.letzgo.LetzgoBe.domain.dataFetcher.entity.Restaurant;
import com.letzgo.LetzgoBe.domain.dataFetcher.repository.HotelRepository;
import com.letzgo.LetzgoBe.domain.dataFetcher.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeocodingService {
    private final GeoApiContext context;
    private final HotelRepository hotelRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public void updateHotelCoordinates() {
        List<Hotel> hotels = hotelRepository.findAll();

        log.info("호텔 좌표정보 추가중입니다.");
        for (Hotel hotel : hotels) {
            Optional<double[]> coordinates = getCoordinates(hotel.getLocation());
            if (coordinates.isPresent()) {
                hotel.setLat(coordinates.get()[0]);
                hotel.setLng(coordinates.get()[1]);
            }
        }
        log.info("호텔 좌표정보 추가완료.");
    }

    @Transactional
    public void updateRestaurantCoordinates() {
        List<Restaurant> restaurants = restaurantRepository.findAll();

        log.info("식당 좌표정보 추가중입니다.");
        for (Restaurant restaurant : restaurants) {
            Optional<double[]> coordinates = getCoordinates(restaurant.getLocation());
            if (coordinates.isPresent()) {
                restaurant.setLat(coordinates.get()[0]);
                restaurant.setLng(coordinates.get()[1]);
            }
        }
        log.info("식당 좌표정보 추가완료.");
    }

    private Optional<double[]> getCoordinates(String address) {
        try {
            GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
            if (results.length > 0) {
                double lat = results[0].geometry.location.lat;
                double lng = results[0].geometry.location.lng;
                return Optional.of(new double[]{lat, lng});
            } else
                return Optional.empty();
        } catch (Exception e) {
            log.warn("지오코딩api 호출중 예외발생");
        }
        return Optional.empty();
    }
}
