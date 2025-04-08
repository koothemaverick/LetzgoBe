package com.letzgo.LetzgoBe.domain.schedule.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleItemDto {
    private Long scheduleItemPk;
    private Long schedulePk;
    private Long itemPk;
    private String itemTypeName; // 장소 or 메모
    private int orderIndex;
    private int sequence;
}