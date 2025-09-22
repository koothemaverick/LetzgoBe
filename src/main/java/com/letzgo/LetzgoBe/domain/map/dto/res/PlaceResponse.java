package com.letzgo.LetzgoBe.domain.map.dto.res;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class PlaceResponse {
    private String name;
    private String address;
    private String placeId;
    private String placePhoto;
    private double lat;
    private double lng;
}
