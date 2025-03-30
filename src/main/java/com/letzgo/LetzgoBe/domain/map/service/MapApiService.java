package com.letzgo.LetzgoBe.domain.map.service;

import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import com.letzgo.LetzgoBe.domain.map.dto.PlaceDetailResponse;
import com.letzgo.LetzgoBe.domain.map.dto.PlaceDto;
import com.letzgo.LetzgoBe.domain.map.entity.Place;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MapApiService {
    private final GeoApiContext context;

    //장소이름, 주소, 사진요청용스트링 받아옴
    public PlaceDto getPlaceDetails(String placeId) throws IOException, InterruptedException, ApiException {

        PlaceDetails placeDetails = PlacesApi.placeDetails(context, placeId)
                .fields(PlaceDetailsRequest.FieldMask.NAME,
                        PlaceDetailsRequest.FieldMask.FORMATTED_ADDRESS,
                        PlaceDetailsRequest.FieldMask.PHOTOS,
                        PlaceDetailsRequest.FieldMask.GEOMETRY_LOCATION_LAT,
                        PlaceDetailsRequest.FieldMask.GEOMETRY_LOCATION_LNG
                ) //무료사용한도:5,000번/한달 그이상 호출시 과금됨
                .language("ko")
                .await();
        log.info("구글API(PlaceAPI(기존)-장소 세부정보) 호출됨");

        String photoRef = null;
        //사진호출용 스트링(최대10개)중 첫번째만사용
        if(placeDetails.photos != null) {
            Photo[] photos = placeDetails.photos;
            photoRef = photos[0].photoReference;
        }

        return PlaceDto.builder()
                .name(placeDetails.name)
                .address(placeDetails.formattedAddress)
                .placeId(placeId)
                .placePhoto(photoRef)
                .lat(placeDetails.geometry.location.lat)
                .lng(placeDetails.geometry.location.lng)
                .build();
    }

    //주변의 장소를 검색
    //파라미터:검색키워드, 사용자위도경도, 검색주위반경(m단위), 받아올 갯수(최대20개)
    public List<PlaceDto> getNearPlaces(String query, String lat, String lng, int radius, int num) throws IOException, InterruptedException, ApiException {

        LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        TextSearchRequest textSearchRequest = PlacesApi.textSearchQuery(context, query, latLng);
        PlacesSearchResponse apiResponse = textSearchRequest
                .radius(radius)
                .language("ko")
                .await(); //무료사용한도:5,000번/한달 그이상 호출시 과금됨
        log.info("구글API(PlaceAPI(기존)-텍스트 검색) 호출됨");


        List<PlaceDto> placeDtos = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            if ((apiResponse.results[i] != null) && (i < 20)) {
                PlaceDto nearPlace = PlaceDto.builder()
                        .name(apiResponse.results[i].name)
                        .address(apiResponse.results[i].formattedAddress)
                        .placeId(apiResponse.results[i].placeId)
                        .placePhoto(apiResponse.results[i].photos[0].photoReference)
                        .lat(apiResponse.results[i].geometry.location.lat)
                        .lng(apiResponse.results[i].geometry.location.lng)
                        .build();

                placeDtos.add(nearPlace);
            }
        }
        return placeDtos;
    }
}

