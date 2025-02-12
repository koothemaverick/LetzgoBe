package com.letzgo.LetzgoBe.map.dto;

import com.letzgo.LetzgoBe.map.entity.Place;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public class ResponseDto {
    private PlaceDto placeinfo;
    @Builder.Default
    private List<ReviewDto> reviews = new ArrayList<>();
}
