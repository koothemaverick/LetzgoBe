package com.letzgo.LetzgoBe.domain.map.controller;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUser;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.map.dto.PlaceDto;
import com.letzgo.LetzgoBe.domain.map.dto.PlaceInfoResponseDto;
import com.letzgo.LetzgoBe.domain.map.dto.ReviewRequestDto;
import com.letzgo.LetzgoBe.domain.map.entity.PlacePage;
import com.letzgo.LetzgoBe.domain.map.service.MapService;
import com.letzgo.LetzgoBe.domain.map.service.ReviewService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import com.letzgo.LetzgoBe.global.common.response.LetzgoPage;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ApiResponse<PlaceInfoResponseDto> getPlaceInfo(@PathVariable("placeId") String placeId) {
        return ApiResponse.of(mapService.findPlaceInfo(placeId));
    }

    //리뷰게시
    @PostMapping("/review/{placeId}")
    public ApiResponse<String> postReview(@LoginUser LoginUserDto loginUserDto,
                                  @PathVariable("placeId") String placeId,
                                  @ModelAttribute ReviewRequestDto reviewRequestDto,
                                  @RequestParam(value = "image", required = false) MultipartFile image) {
        reviewService.createReview(loginUserDto, placeId, reviewRequestDto, image);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    //리뷰수정
    @PatchMapping("/review/{reviewId}")
    public ApiResponse<String> patchReview(@LoginUser LoginUserDto loginUserDto,
                                   @PathVariable("reviewId") Long reviewId,
                                   @ModelAttribute ReviewRequestDto reviewRequestDto,
                                   @RequestParam(value = "image", required = false) MultipartFile image) {
        reviewService.updateReview(loginUserDto, reviewId, reviewRequestDto, image);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    //리뷰삭제
    @DeleteMapping("/review/{reviewId}")
    public ApiResponse<String> deleteReview(@LoginUser LoginUserDto loginUserDto, @PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(loginUserDto, reviewId);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    //검색으로 장소 목록 불러옴
    //파라미터:검색키워드, 사용자위도경도, 검색주위반경(m단위), 받아올 갯수(최대20개)
    @GetMapping("/places")
    public ApiResponse<PlaceDto> getSearchedPlaces(@RequestParam("query") String query,
                                                   @RequestParam("lat") String lat,
                                                   @RequestParam("lng") String lng,
                                                   @RequestParam("radius") int radius,
                                                   @ModelAttribute PlacePage request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.of(LetzgoPage.of(mapService.getSearchedPlaces(query, lat, lng, radius, pageable)));
    }
}
