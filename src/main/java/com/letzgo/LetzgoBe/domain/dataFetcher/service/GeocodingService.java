package com.letzgo.LetzgoBe.domain.dataFetcher.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.letzgo.LetzgoBe.domain.dataFetcher.dto.res.NcpGeocodeResponse;
import com.letzgo.LetzgoBe.domain.dataFetcher.entity.Hotel;
import com.letzgo.LetzgoBe.domain.dataFetcher.entity.Restaurant;
import com.letzgo.LetzgoBe.domain.dataFetcher.repository.HotelRepository;
import com.letzgo.LetzgoBe.domain.dataFetcher.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeocodingService {
    private final GeoApiContext context;
    private final HotelRepository hotelRepository;
    private final RestaurantRepository restaurantRepository;
    private final WebClient naverApiClient;

    @Transactional
    public void updateHotelCoordinates() {
        List<Hotel> hotels = hotelRepository.findAll();

        log.info("호텔 좌표정보 추가중입니다.");
        for (Hotel hotel : hotels) {
            Optional<double[]> coordinates = getCoordinatesFromNaver(hotel.getLocation());
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

        log.info("음식점 좌표정보 추가중입니다.");
        for (Restaurant restaurant : restaurants) {
            Optional<double[]> coordinates = getCoordinatesFromNaver(restaurant.getLocation());
            if (coordinates.isPresent()) {
                restaurant.setLat(coordinates.get()[0]);
                restaurant.setLng(coordinates.get()[1]);
            }
        }
        log.info("음식점 좌표정보 추가완료.");
    }

    private Optional<double[]> getCoordinatesFromGoogle(String address) {
        try {
            GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
            if (results.length > 0) {
                double lat = results[0].geometry.location.lat;
                double lng = results[0].geometry.location.lng;
                return Optional.of(new double[]{lat, lng});
            }
        }catch (Exception e) {
            log.warn("지오코딩api 호출중 예외발생");
        }
        return Optional.empty();
    }

    public Optional<double[]> getCoordinatesFromNaver(String address) {
        try {
            NcpGeocodeResponse response = naverApiClient.get()
                    .uri(uriBuilder -> uriBuilder.queryParam("query", address).build())
                    .retrieve()
                    .bodyToMono(NcpGeocodeResponse.class)
                    .block();

            if (response != null && response.getAddresses() != null && !response.getAddresses().isEmpty()) {
                NcpGeocodeResponse.Address addr = response.getAddresses().get(0);
                double lng = Double.parseDouble(addr.getX());
                double lat = Double.parseDouble(addr.getY());
                return Optional.of(new double[]{lat, lng});
            }

        } catch (Exception e) {
            log.warn("지오코딩 호출 중 예외 발생: {}", e.getMessage());
        }

        return Optional.empty();
    }

}
