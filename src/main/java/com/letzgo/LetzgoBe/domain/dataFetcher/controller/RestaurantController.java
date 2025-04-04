package com.letzgo.LetzgoBe.domain.dataFetcher.controller;

import com.letzgo.LetzgoBe.domain.dataFetcher.service.RestaurantInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurant")
public class RestaurantController {
    private final RestaurantInfoService restaurantInfoService;
    @GetMapping("/test")
    void test() {
        restaurantInfoService.getRestaurantsInfo();
    }
}
