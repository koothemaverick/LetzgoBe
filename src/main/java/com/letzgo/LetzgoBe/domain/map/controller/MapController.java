package com.letzgo.LetzgoBe.domain.map.controller;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUser;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.map.dto.PlaceInfoResponseDto;
import com.letzgo.LetzgoBe.domain.map.dto.ReviewDto;
import com.letzgo.LetzgoBe.domain.map.service.MapService;
import com.letzgo.LetzgoBe.domain.map.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/map-api")
public class MapController {
    private final MapService mapService;
    private final ReviewService reviewService;

    //장소에 대한 정보출력
    @GetMapping("/place/{placeId}")
    public ResponseEntity getPlaceInfo(@RequestParam String placeId) {

        PlaceInfoResponseDto placeInfo = mapService.findPlaceInfo(placeId);

        return ResponseEntity.status(HttpStatus.OK).body(placeInfo);
    }
    //리뷰게시
    @PostMapping("/review/{placeId}")
    public ResponseEntity postReview(@LoginUser LoginUserDto loginUserDto, @RequestParam String placeId, @RequestBody ReviewDto reviewDto) {
        boolean review = reviewService.createReview(loginUserDto, placeId, reviewDto);
        if (review) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    //리뷰수정
    @PatchMapping("/review")
    public ResponseEntity patchReview(@LoginUser LoginUserDto loginUserDto, @RequestBody ReviewDto reviewDto) {
        reviewService.updateReview(loginUserDto, reviewDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //리뷰삭제
    @DeleteMapping("/review")
    public ResponseEntity deleteReview(@LoginUser LoginUserDto loginUserDto, @RequestBody ReviewDto reviewDto) {
        reviewService.deleteReview(loginUserDto, reviewDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
