package com.letzgo.LetzgoBe.domain.map.dto;

import com.letzgo.LetzgoBe.domain.map.entity.Place;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class PlaceDto {
    private String name;
    private String address;
    private String placePhoto;

    public static PlaceDto entityToDto(Place place) {
        return PlaceDto.builder()
                .name(place.getName())
                .address(place.getAddress())
                .placePhoto(place.getPlacePhoto())
                .build();
    }
}
