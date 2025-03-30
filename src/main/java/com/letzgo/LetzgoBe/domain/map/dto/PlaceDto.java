package com.letzgo.LetzgoBe.domain.map.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class PlaceDto {
    private String name;
    private String address;
    private String placeId;
    private String placePhoto;
    private double lat;
    private double lng;
}