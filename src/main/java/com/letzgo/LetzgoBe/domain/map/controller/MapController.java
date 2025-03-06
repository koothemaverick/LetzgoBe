package com.letzgo.LetzgoBe.domain.map.controller;

import com.letzgo.LetzgoBe.domain.map.dto.ReviewDto;
import com.letzgo.LetzgoBe.domain.map.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/map-api")
public class MapController {
    private final MapService mapService;

    //클릭한 장소에 대한 정보,리뷰 출력
    @GetMapping("/place")
    public ResponseEntity getPlaceInfo(long latitude, long longitude) {

        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    //검색한 장소에 대한 정보, 리뷰 출력
    @GetMapping("/place-search/{placeName}")
    public ResponseEntity getSearchedPlaceInfo(@PathVariable String placeName) {
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    //리뷰게시
    @PostMapping("/review")
    public ResponseEntity postReview(@RequestBody ReviewDto reviewDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body("");
    }

    //리뷰수정
    @PatchMapping("/review")
    public ResponseEntity patchReview(@RequestBody ReviewDto reviewDto) {
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    //리뷰삭제
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity deleteReview(@PathVariable int reviewId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }
}
