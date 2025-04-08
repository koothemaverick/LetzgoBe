package com.letzgo.LetzgoBe.domain.schedule.controller;

/**
 * 일정 내 항목(장소/메모) 추가 및 조회를 처리하는 컨트롤러
 */

import com.letzgo.LetzgoBe.domain.schedule.dto.ScheduleItemDto;
import com.letzgo.LetzgoBe.domain.schedule.entity.ScheduleItem;
import com.letzgo.LetzgoBe.domain.schedule.service.ScheduleItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules/{schedulePk}/items")
@RequiredArgsConstructor
public class ScheduleItemController {

    private final ScheduleItemService scheduleItemService;

    /**
     * 일정에 장소 또는 메모 항목 추가
     */
    @PostMapping
    public ResponseEntity<ScheduleItemDto> addItem(
            @PathVariable Long schedulePk,
            @RequestBody ScheduleItemDto dto
    ) {
        ScheduleItem saved = scheduleItemService.addItem(
                schedulePk,
                dto.getItemPk(),
                dto.getItemTypeName(),
                dto.getOrderIndex()
        );

        ScheduleItemDto response = new ScheduleItemDto();
        response.setScheduleItemPk(saved.getScheduleItemPk());
        response.setSchedulePk(schedulePk);
        response.setItemPk(saved.getItemPk());
        response.setItemTypeName(saved.getItemType().getTypeName());
        response.setOrderIndex(saved.getOrderIndex());
        response.setSequence(saved.getSequence());

        return ResponseEntity.ok(response);
    }

    /**
     * 일정에 등록된 항목(장소+메모) 전체 조회 (순서대로)
     */
    @GetMapping
    public ResponseEntity<List<ScheduleItemDto>> getItems(@PathVariable Long schedulePk) {
        List<ScheduleItemDto> itemList = scheduleItemService.getItemDtosBySchedule(schedulePk);
        return ResponseEntity.ok(itemList);
    }
}