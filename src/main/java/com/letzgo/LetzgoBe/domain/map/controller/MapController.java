package com.letzgo.LetzgoBe.domain.map.controller;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUser;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.map.dto.PlaceInfoResponseDto;
import com.letzgo.LetzgoBe.domain.map.dto.ReviewRequestDto;
import com.letzgo.LetzgoBe.domain.map.service.MapService;
import com.letzgo.LetzgoBe.domain.map.service.ReviewService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@RequestMapping("/map-api")
public class MapController {
    private final MapService mapService;
    private final ReviewService reviewService;

    //장소에 대한 정보출력
    @GetMapping("/place/{placeId}")
    public ApiResponse getPlaceInfo(@PathVariable("placeId") String placeId) {

        PlaceInfoResponseDto placeInfo = mapService.findPlaceInfo(placeId);

        return ApiResponse.of(placeInfo);
    }
    //리뷰게시
    @PostMapping("/review/{placeId}")
    public ApiResponse postReview(@LoginUser LoginUserDto loginUserDto,
                                  @PathVariable("placeId") String placeId,
                                  @ModelAttribute ReviewRequestDto reviewRequestDto,
                                  @RequestParam(value = "image", required = false) MultipartFile image) {
        reviewService.createReview(loginUserDto, placeId, reviewRequestDto, image);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    //리뷰수정
    @PatchMapping("/review/{reviewId}")
    public ApiResponse patchReview(@LoginUser LoginUserDto loginUserDto,
                                   @PathVariable("reviewId") Long reviewId,
                                   @ModelAttribute ReviewRequestDto reviewRequestDto,
                                   @RequestParam(value = "image", required = false) MultipartFile image) {
        reviewService.updateReview(loginUserDto, reviewId, reviewRequestDto, image);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    //리뷰삭제
    @DeleteMapping("/review/{reviewId}")
    public ApiResponse deleteReview(@LoginUser LoginUserDto loginUserDto, @PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(loginUserDto, reviewId);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }
}
