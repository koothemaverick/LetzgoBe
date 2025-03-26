package com.letzgo.LetzgoBe.domain.recommend.controller;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUser;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.map.dto.PlaceDto;
import com.letzgo.LetzgoBe.domain.recommend.service.PlaceRecommendService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendController {
    private final PlaceRecommendService placeRecommendService;
    @GetMapping("/recommend/{num}")
    public ApiResponse getRecommendedPlaces(@LoginUser LoginUserDto loginUserDto, @PathVariable("num") int num) {
        List<PlaceDto> recommededPlace = placeRecommendService.getRecommededPlace(loginUserDto, num);
        return ApiResponse.of(recommededPlace);
    }
}
