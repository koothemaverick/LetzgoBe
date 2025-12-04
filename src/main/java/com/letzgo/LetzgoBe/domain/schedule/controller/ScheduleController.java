package com.letzgo.LetzgoBe.domain.schedule.controller;

import com.letzgo.LetzgoBe.domain.schedule.dto.ScheduleDto;
import com.letzgo.LetzgoBe.domain.schedule.entity.Schedule;
import com.letzgo.LetzgoBe.domain.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Schedule", description = "일정 API")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody ScheduleDto dto) {
        System.out.println(" [컨트롤러] 받은 ScheduleDto: " +
                "hostAccountPk=" + dto.getHostAccountPk() +
                ", region=" + dto.getRegion() +
                ", title=" + dto.getTitle() +
                ", startDate=" + dto.getStartDate() +
                ", endDate=" + dto.getEndDate());

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
    public ResponseEntity<List<ScheduleDto>> getAll(@RequestParam("memberId") Long memberId) {
        List<Schedule> schedules = scheduleService.getAllSchedules(memberId);
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
