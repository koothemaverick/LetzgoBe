package com.letzgo.LetzgoBe.domain.map.service;

import com.letzgo.LetzgoBe.domain.map.dto.PlaceDto;
import com.letzgo.LetzgoBe.domain.map.entity.Place;
import com.letzgo.LetzgoBe.domain.map.repository.PlaceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MapService {
    private final PlaceRepository placeRepository;
    private final MapApiService MapApiService;

    public PlaceDto findPlaceInfo(long latitude, long longitude) {
        Optional<Place> place = placeRepository.findByLatitudeAndLongitude(latitude, longitude);
        PlaceDto placeDto = new PlaceDto("1","2","3"); //에러방지용 임시코드


        if (place.isPresent()) {
        }

        else {

        }
        return placeDto;
    }

}
