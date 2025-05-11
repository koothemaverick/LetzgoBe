package com.letzgo.LetzgoBe.domain.schedule.controller;



import com.letzgo.LetzgoBe.domain.schedule.dto.ScheduleMemoDto;
import com.letzgo.LetzgoBe.domain.schedule.dto.SchedulePlaceDto;
import com.letzgo.LetzgoBe.domain.schedule.service.ScheduleItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules/{schedulePk}")
@RequiredArgsConstructor
public class ScheduleItemController {

    private final ScheduleItemService scheduleService;

    /** 장소 추가 */
    @PostMapping("/places")
    public ResponseEntity<Long> addPlace(@PathVariable("schedulePk") Long schedulePk, @RequestBody SchedulePlaceDto dto) {
        Long savedPk = scheduleService.addPlace(schedulePk, dto);
        return ResponseEntity.ok(savedPk);
    }

    /** 메모 추가 */
    @PostMapping("/places/{schedulePlacePk}/memo")
    public ResponseEntity<Long> addMemo(@PathVariable("schedulePlacePk") Long schedulePlacePk, @RequestBody ScheduleMemoDto dto) {
        Long savedPk = scheduleService.addMemo(schedulePlacePk, dto);
        return ResponseEntity.ok(savedPk);
    }

    /** 장소 삭제 */
    @DeleteMapping("/places/{schedulePlacePk}")
    public ResponseEntity<Void> deletePlace(@PathVariable("schedulePlacePk") Long schedulePlacePk) {
        scheduleService.deletePlace(schedulePlacePk);
        return ResponseEntity.noContent().build();
    }

    /** 메모 삭제 */
    @DeleteMapping("/places/{schedulePlacePk}/memo")
    public ResponseEntity<Void> deleteMemo(@PathVariable("schedulePlacePk") Long schedulePlacePk) {
        scheduleService.deleteMemo(schedulePlacePk);
        return ResponseEntity.noContent().build();
    }

    /** 장소 전체 조회 */
    @GetMapping("/places")
    public ResponseEntity<List<SchedulePlaceDto>> getPlaces(@PathVariable("schedulePk") Long schedulePk) {
        return ResponseEntity.ok(scheduleService.getPlaces(schedulePk));
    }

    /** 장소 + 메모 Map 형태 조회 */
    @GetMapping("/places/map")
    public ResponseEntity<Map<Long, SchedulePlaceDto>> getPlacesAsMap(@PathVariable("schedulePk") Long schedulePk) {
        List<SchedulePlaceDto> placeDtos = scheduleService.getPlaces(schedulePk);
        Map<Long, SchedulePlaceDto> map = placeDtos.stream()
                .collect(Collectors.toMap(SchedulePlaceDto::getSchedulePlacePk, dto -> dto));
        return ResponseEntity.ok(map);
    }
}
