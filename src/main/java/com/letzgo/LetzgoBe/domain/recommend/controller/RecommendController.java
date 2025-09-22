package com.letzgo.LetzgoBe.domain.recommend.controller;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUser;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.map.dto.res.PlaceResponse;
import com.letzgo.LetzgoBe.domain.map.entity.PlacePage;
import com.letzgo.LetzgoBe.domain.recommend.service.RecommendService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import com.letzgo.LetzgoBe.global.common.response.LetzgoPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/rest-api/v1/recommend")
@RequiredArgsConstructor
public class RecommendController {
    private final RecommendService recommendService;

    @GetMapping
    public ApiResponse<PlaceResponse> getRecommendedPlaces(@LoginUser LoginUserDto loginUserDto, @ModelAttribute PlacePage request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.of(LetzgoPage.of(recommendService.getRecommendedPlace(loginUserDto, pageable)));
    }
}
