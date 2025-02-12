package com.letzgo.LetzgoBe.domain.map.map.dto;

import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public class ResponseDto {
    private PlaceDto placeinfo;
    @Builder.Default
    private List<ReviewDto> reviews = new ArrayList<>();
}
