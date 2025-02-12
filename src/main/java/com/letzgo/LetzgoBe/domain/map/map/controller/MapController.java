package com.letzgo.LetzgoBe.domain.map.map.controller;

import com.letzgo.LetzgoBe.domain.map.map.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mapApi")
public class MapController {
    final private MapService mapService;

    @GetMapping("/place")
    public ResponseEntity getPlaceInfo(long latitude, long longitude) {

        return ResponseEntity.ok().body("");
    }

}
