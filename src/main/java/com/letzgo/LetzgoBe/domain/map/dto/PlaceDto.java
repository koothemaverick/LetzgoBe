package com.letzgo.LetzgoBe.domain.map.dto;

import lombok.*;

@ToString
@Builder
public class PlaceDto {
    private String name;
    private String detail;
    private String address;
}
