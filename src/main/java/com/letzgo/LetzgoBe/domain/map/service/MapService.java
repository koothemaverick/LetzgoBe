package com.letzgo.LetzgoBe.domain.map.service;

import com.letzgo.LetzgoBe.domain.map.dto.PlaceDto;
import com.letzgo.LetzgoBe.domain.map.dto.PlaceInfoResponseDto;
import com.letzgo.LetzgoBe.domain.map.dto.ReviewResponseDto;
import com.letzgo.LetzgoBe.domain.map.entity.Place;
import com.letzgo.LetzgoBe.domain.map.entity.Review;
import com.letzgo.LetzgoBe.domain.map.repository.PlaceRepository;
import com.letzgo.LetzgoBe.domain.map.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public PlaceInfoResponseDto findPlaceInfo(String placeId) {

        PlaceDto placeDto = null;

        //api호출해 정보 받아옴, placeId제외 캐싱x
        try {
            placeDto = mapApiService.getPlaceDetails(placeId);
        }
        catch (Exception e) {
            throw new RuntimeException("구글 api호출중 오류발생");
        }

        Place place = placeRepository.findByPlaceId(placeId);

        if (place != null) { //한번이상 조회된적있는 장소일경우
            List<Review> reviews = reviewRepository.findByPlace(place);
            List<ReviewResponseDto> reviewResponseDtos = reviews.stream()
                    .map(review -> ReviewResponseDto.entitytoDto(review))
                    .collect(Collectors.toList());

            return PlaceInfoResponseDto.builder()
                    .placeinfo(placeDto)
                    .reviews(reviewResponseDtos)
                    .build();
        }

        else { //처음 조회하는 장소일 경우 db에 placeId저장
                Place newPlace = Place.builder()
                        .placeId(placeId)
                        .build();
                placeRepository.save(newPlace);

                return PlaceInfoResponseDto.builder()
                        .placeinfo(placeDto)
                        .build();
        }
    }
}


