package com.letzgo.LetzgoBe.domain.schedule.controller;

import com.letzgo.LetzgoBe.domain.schedule.dto.ScheduleDto;
import com.letzgo.LetzgoBe.domain.schedule.entity.Schedule;
import com.letzgo.LetzgoBe.domain.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Void> delete(@PathVariable("schedulePk") Long schedulePk) {
        scheduleService.deleteSchedule(schedulePk);
        return ResponseEntity.noContent().build();
    }

    /** 일정 전체 조회 API */
    @GetMapping
    public ResponseEntity<List<ScheduleDto>> getAll() {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        List<ScheduleDto> result = schedules.stream().map(schedule -> {
            ScheduleDto dto = new ScheduleDto();
            dto.setSchedulePk(schedule.getSchedulePk());
            dto.setHostAccountPk(schedule.getHostAccount().getId());
            dto.setRegion(schedule.getRegion());
            dto.setTitle(schedule.getTitle());
            dto.setStartDate(schedule.getStartDate());
            dto.setEndDate(schedule.getEndDate());
            return dto;
        }).toList();

        return ResponseEntity.ok(result);
    }

    /** 일정 단건 조회 API */
    @GetMapping("/{schedulePk}")
    public ResponseEntity<ScheduleDto> getOne(@PathVariable("schedulePk") Long schedulePk) {
        Schedule schedule = scheduleService.getSchedule(schedulePk)
                .orElseThrow(() -> new RuntimeException("해당 일정이 존재하지 않습니다"));

        ScheduleDto dto = new ScheduleDto();
        dto.setSchedulePk(schedule.getSchedulePk());
        dto.setHostAccountPk(schedule.getHostAccount().getId());
        dto.setRegion(schedule.getRegion());
        dto.setTitle(schedule.getTitle());
        dto.setStartDate(schedule.getStartDate());
        dto.setEndDate(schedule.getEndDate());

        return ResponseEntity.ok(dto);
    }

}
