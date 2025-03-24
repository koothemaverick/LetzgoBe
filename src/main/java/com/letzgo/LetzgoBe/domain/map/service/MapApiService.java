package com.letzgo.LetzgoBe.domain.map.service;

import com.google.maps.GeoApiContext;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.Photo;
import com.google.maps.model.PlaceDetails;
import com.letzgo.LetzgoBe.domain.map.dto.PlaceDetailResponse;
import com.letzgo.LetzgoBe.domain.map.dto.PlaceDto;
import com.letzgo.LetzgoBe.domain.map.entity.Place;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class MapApiService {
    private final GeoApiContext context;

    //장소이름, 주소, 사진요청용스트링 받아옴
    public PlaceDto getPlaceDetails(String placeId) throws IOException, InterruptedException, ApiException {

        log.info("구글API 호출됨");
        PlaceDetails placeDetails = PlacesApi.placeDetails(context, placeId)
                .fields(PlaceDetailsRequest.FieldMask.NAME,
                        PlaceDetailsRequest.FieldMask.FORMATTED_ADDRESS,
                        PlaceDetailsRequest.FieldMask.PHOTOS) //무료사용한도:1000번/한달 그이상 호출시 과금됨
                .language("ko")
                .await();

        String photoRef = null;
        //사진호출용 스트링(최대10개)중 첫번째만사용
        if(placeDetails.photos != null) {
            Photo[] photos = placeDetails.photos;
            photoRef = photos[0].photoReference;
        }

        return PlaceDto.builder()
                .name(placeDetails.name)
                .address(placeDetails.formattedAddress)
                .placePhoto(photoRef)
                .build();
    }
}

