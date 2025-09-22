package com.letzgo.LetzgoBe.domain.map.service;

import com.letzgo.LetzgoBe.domain.map.dto.res.PlaceResponse;
import com.letzgo.LetzgoBe.domain.map.dto.res.PlaceInfoResponse;
import com.letzgo.LetzgoBe.domain.map.dto.res.ReviewResponse;
import com.letzgo.LetzgoBe.domain.map.entity.Place;
import com.letzgo.LetzgoBe.domain.map.entity.PlacePage;
import com.letzgo.LetzgoBe.domain.map.entity.Review;
import com.letzgo.LetzgoBe.domain.map.repository.PlaceRepository;
import com.letzgo.LetzgoBe.domain.map.repository.ReviewRepository;
import com.letzgo.LetzgoBe.global.common.response.PageResponse;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import com.letzgo.LetzgoBe.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MapService {
    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;
    private final MapApiService mapApiService;

    //placeId를 받아 해당 장소에 대한 정보, 리뷰로 이루어진 dto 반환
    @Transactional(readOnly = true)
    public PlaceInfoResponse findPlaceInfo(String placeId) {

        PlaceResponse placeResponse = null;

        //api호출해 정보 받아옴, placeId제외 캐싱x
        try {
            placeResponse = mapApiService.getPlaceDetails(placeId);
        }
        catch (Exception e) {
            throw new RuntimeException("구글 api호출중 오류발생");
        }

        Place place = placeRepository.findByPlaceId(placeId);

        if (place != null) { //한번이상 조회된적있는 장소일경우
            List<Review> reviews = reviewRepository.findByPlace(place);
            List<ReviewResponse> reviewResponses = reviews.stream()
                    .map(review -> ReviewResponse.entitytoDto(review))
                    .collect(Collectors.toList());

            return PlaceInfoResponse.builder()
                    .placeinfo(placeResponse)
                    .reviews(reviewResponses)
                    .build();
        }

        else { //처음 조회하는 장소일 경우 db에 placeId저장
                Place newPlace = Place.builder()
                        .placeId(placeId)
                        .build();
                placeRepository.save(newPlace);

                return PlaceInfoResponse.builder()
                        .placeinfo(placeResponse)
                        .build();
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<PlaceResponse> getSearchedPlaces(String query, String lat, String lng, int radius, Pageable pageable){
        checkPageSize(pageable.getPageSize());
        try {
            return PageResponse.of(mapApiService.getNearPlaces(query, lat, lng, radius, pageable));
        } catch (Exception e) {
            throw new RuntimeException("구글 api호출중 오류발생");
        }
    }

    // ----------------- 헬퍼 메서드 -----------------

    // 요청 페이지 수 제한
    public void checkPageSize(int pageSize) {
        int maxPageSize = PlacePage.getMaxPageSize();
        if (pageSize > maxPageSize) {
            throw new ServiceException(ReturnCode.PAGE_REQUEST_FAIL);
        }
    }
}


