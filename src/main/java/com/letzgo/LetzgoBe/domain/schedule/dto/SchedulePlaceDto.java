package com.letzgo.LetzgoBe.domain.schedule.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulePlaceDto {
    private Long schedulePlacePk;
    private String name;
    private String address;
    private String placeId;
    private double latitude;
    private double longitude;
    private int orderIndex;
    private int sequence;
    private Long memoPk;         // 메모 PK
    private String memoContent;  // 메모 내용
}
