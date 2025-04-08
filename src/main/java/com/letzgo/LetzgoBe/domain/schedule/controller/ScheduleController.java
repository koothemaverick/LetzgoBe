package com.letzgo.LetzgoBe.domain.schedule.controller;

import com.letzgo.LetzgoBe.domain.schedule.dto.ScheduleDto;
import com.letzgo.LetzgoBe.domain.schedule.entity.Schedule;
import com.letzgo.LetzgoBe.domain.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 일정 CRUD를 담당하는 컨트롤러
 */
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /** 일정 생성 API */
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody ScheduleDto dto) {
        Schedule schedule = scheduleService.createSchedule(dto);
        return ResponseEntity.ok(schedule.getSchedulePk());
    }

    /** 일정 삭제 API */
    @DeleteMapping("/{schedulePk}")
    public ResponseEntity<Void> delete(@PathVariable Long schedulePk) {
        scheduleService.deleteSchedule(schedulePk);
        return ResponseEntity.noContent().build();
    }
}
