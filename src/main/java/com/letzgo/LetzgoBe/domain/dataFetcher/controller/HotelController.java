package com.letzgo.LetzgoBe.domain.dataFetcher.controller;

import com.letzgo.LetzgoBe.domain.dataFetcher.service.HotelInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hotel")
public class HotelController {
    private final HotelInfoService hotelInfoService;
    @GetMapping("/test")
    void test() {
        hotelInfoService.getHotelsInfo(1);
    }
}
