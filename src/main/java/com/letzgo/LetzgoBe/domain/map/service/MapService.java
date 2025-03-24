package com.letzgo.LetzgoBe.domain.map.service;

import com.google.maps.model.Photo;
import com.letzgo.LetzgoBe.domain.map.dto.PlaceDetailResponse;
import com.letzgo.LetzgoBe.domain.map.dto.PlaceDto;
import com.letzgo.LetzgoBe.domain.map.dto.PlaceInfoResponseDto;
import com.letzgo.LetzgoBe.domain.map.dto.ReviewDto;
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

        Place place = placeRepository.findByPlaceId(placeId);
        if (place != null) { //한번이상 조회된적있는 장소일경우

            PlaceDto placeDto = PlaceDto.entityToDto(place);

            List<Review> reviews = reviewRepository.findByPlace(place);
            List<ReviewDto> ReviewDtos = reviews.stream()
                    .map(review -> ReviewDto.entitytoDto(review))
                    .collect(Collectors.toList());

            return PlaceInfoResponseDto.builder()
                    .placeinfo(placeDto)
                    .reviews(ReviewDtos)
                    .build();
        }

        else { //처음 조회하는 장소일 경우
            PlaceDetailResponse placeDetail = null;

            //api호출해 정보 받아옴
            try {
                placeDetail = mapApiService.getPlaceDetails(placeId);
            }
             catch (Exception e) {
                throw new RuntimeException("구글 api호출중 오류발생");
            }

            String photoRef = null;
            //사진호출용 스트링(최대10개)중 첫번째만사용
            if(placeDetail.getPhotos() != null) {
                Photo[] photos = placeDetail.getPhotos();
                photoRef = photos[0].photoReference;
            }

            if(placeDetail.getPlaceName() != null && placeDetail.getAddress() != null) {
                //db에 저장

                Place newPlace = Place.builder()
                        .name(placeDetail.getPlaceName())
                        .address(placeDetail.getAddress())
                        .placeId(placeId)
                        .placePhoto(photoRef)
                        .build();
                placeRepository.save(newPlace);
                //반환
                PlaceDto placeDto = PlaceDto.entityToDto(newPlace);
                return PlaceInfoResponseDto.builder()
                        .placeinfo(placeDto)
                        .build();
            }
            else {
                throw new RuntimeException("placeDetail의 Name혹은 Address가 null임");
            }
        }
    }
}
