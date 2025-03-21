package com.letzgo.LetzgoBe.domain.map.service;

import com.google.maps.GeoApiContext;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.Photo;
import com.google.maps.model.PlaceDetails;
import com.letzgo.LetzgoBe.domain.map.dto.PlaceDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class MapApiService {
    private final GeoApiContext context;

    //장소이름, 주소, 사진요청용스트링 받아옴
    public PlaceDetailResponse getPlaceDetails(String placeId) throws IOException, InterruptedException, ApiException {

        PlaceDetails placeDetails = PlacesApi.placeDetails(context, placeId)
                .fields(PlaceDetailsRequest.FieldMask.NAME,
                        PlaceDetailsRequest.FieldMask.FORMATTED_ADDRESS,
                        PlaceDetailsRequest.FieldMask.PHOTOS)
                .language("ko")
                .await();

        return PlaceDetailResponse.builder()
                .placeName(placeDetails.name)
                .address(placeDetails.adrAddress)
                .photos(placeDetails.photos)
                .build();
    }

    public void savePlacePhoto(String photo) {
        // photo api로 사진 받아와 스토리지에 저장하는 메소드
    }
}

