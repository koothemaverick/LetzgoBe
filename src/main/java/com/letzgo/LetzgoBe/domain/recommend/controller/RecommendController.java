package com.letzgo.LetzgoBe.domain.recommend.controller;

import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUser;
import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.map.dto.res.PlaceResponse;
import com.letzgo.LetzgoBe.domain.map.entity.PlacePage;
import com.letzgo.LetzgoBe.domain.recommend.service.RecommendService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest-api/v1/recommend")
@RequiredArgsConstructor
public class RecommendController {
    private final RecommendService recommendService;

    @GetMapping
    public ApiResponse<List<PlaceResponse>> getRecommendedPlaces(@CurrentUser CurrentUserDto currentUserDto, @ModelAttribute PlacePage request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(recommendService.getRecommendedPlace(currentUserDto, pageable));
    }
}
