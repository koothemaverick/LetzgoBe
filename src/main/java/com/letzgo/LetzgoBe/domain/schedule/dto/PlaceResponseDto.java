package com.letzgo.LetzgoBe.domain.schedule.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceResponseDto {
    private Long placePk;
    private String name;
    private double latitude;
    private double longitude;
}
