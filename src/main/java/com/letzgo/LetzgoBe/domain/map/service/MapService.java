package com.letzgo.LetzgoBe.domain.map.service;

import com.letzgo.LetzgoBe.domain.map.apiClient.NaverApiClient;
import com.letzgo.LetzgoBe.domain.map.dto.PlaceDto;
import com.letzgo.LetzgoBe.domain.map.entity.Place;
import com.letzgo.LetzgoBe.domain.map.repository.PlaceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MapService {
    private final PlaceRepository placeRepository;
    private final NaverApiClient naverApiClient;

    public PlaceDto findPlaceInfo(long latitude, long longitude) {
        Optional<Place> place = placeRepository.findByLatitudeAndLongitude(latitude, longitude);
        PlaceDto placeDto;

        //위도,경도로 장소검색한 결과가 존재할시 그대로 리턴
        if (place.isPresent()) {
            placeDto = PlaceDto.builder()
                    .name(place.get().getName())
                    .address(place.get().getAddress())
                    .detail(place.get().getDetail())
                    .build();
        }
        //없을시 장소데이터 생성해 db에 저장한후 리턴
        else {
            String address = naverApiClient.doReverseGeocoding(latitude, longitude);

        }
        return placeDto;
    }

}
