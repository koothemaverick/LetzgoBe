package com.letzgo.LetzgoBe.domain.map.map.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
public class PlaceDto {
    private String name;
    private String detail;
    private String address;
}
