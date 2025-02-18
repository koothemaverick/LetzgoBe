package com.letzgo.LetzgoBe.domain.map.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@ToString
@Builder
public class PlaceInfoResponseDto {
    private PlaceDto placeinfo;
    @Builder.Default
    private List<ReviewDto> reviews = new ArrayList<>();
}
