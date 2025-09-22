package com.letzgo.LetzgoBe.domain.map.controller;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUser;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.map.dto.res.PlaceResponse;
import com.letzgo.LetzgoBe.domain.map.dto.res.PlaceInfoResponse;
import com.letzgo.LetzgoBe.domain.map.dto.req.ReviewRequest;
import com.letzgo.LetzgoBe.domain.map.entity.PlacePage;
import com.letzgo.LetzgoBe.domain.map.service.MapService;
import com.letzgo.LetzgoBe.domain.map.service.ReviewService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/map-api")
public class MapController {
    private final MapService mapService;
    private final ReviewService reviewService;

    //장소에 대한 정보출력
    @GetMapping("/place/{placeId}")
    public ApiResponse<PlaceInfoResponse> getPlaceInfo(@PathVariable("placeId") String placeId) {
        return ApiResponse.success(mapService.findPlaceInfo(placeId));
    }

    //리뷰게시
    @PostMapping("/review/{placeId}")
    public ApiResponse<Void> postReview(@LoginUser LoginUserDto loginUserDto,
                                  @PathVariable("placeId") String placeId,
                                  @ModelAttribute ReviewRequest reviewRequest,
                                  @RequestParam(value = "image", required = false) MultipartFile image) {
        reviewService.createReview(loginUserDto, placeId, reviewRequest, image);
        return ApiResponse.success();
    }

    //리뷰수정
    @PatchMapping("/review/{reviewId}")
    public ApiResponse<Void> patchReview(@LoginUser LoginUserDto loginUserDto,
                                   @PathVariable("reviewId") Long reviewId,
                                   @ModelAttribute ReviewRequest reviewRequest,
                                   @RequestParam(value = "image", required = false) MultipartFile image) {
        reviewService.updateReview(loginUserDto, reviewId, reviewRequest, image);
        return ApiResponse.success();
    }

    //리뷰삭제
    @DeleteMapping("/review/{reviewId}")
    public ApiResponse<Void> deleteReview(@LoginUser LoginUserDto loginUserDto, @PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(loginUserDto, reviewId);
        return ApiResponse.success();
    }

    //검색으로 장소 목록 불러옴
    //파라미터:검색키워드, 사용자위도경도, 검색주위반경(m단위), 받아올 갯수(최대20개)
    @GetMapping("/places")
    public ApiResponse<List<PlaceResponse>> getSearchedPlaces(@RequestParam("query") String query,
                                                             @RequestParam("lat") String lat,
                                                             @RequestParam("lng") String lng,
                                                             @RequestParam("radius") int radius,
                                                             @ModelAttribute PlacePage request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(mapService.getSearchedPlaces(query, lat, lng, radius, pageable));
    }
}
