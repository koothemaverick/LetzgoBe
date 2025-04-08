package com.letzgo.LetzgoBe.domain.schedule.controller;

import com.letzgo.LetzgoBe.domain.schedule.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 장소 간 경로 최적화를 위한 API 컨트롤러
 */
@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    /** 일정 내 장소들의 최적 경로 순서 계산 요청 */
    @PostMapping("/optimize/{schedulePk}")
    public ResponseEntity<Void> optimize(@PathVariable Long schedulePk) {
        routeService.optimizeSchedulePlaces(schedulePk);
        return ResponseEntity.ok().build();
    }
}