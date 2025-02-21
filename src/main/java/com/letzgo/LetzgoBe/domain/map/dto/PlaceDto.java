package com.letzgo.LetzgoBe.domain.map.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class PlaceDto {
    private String name;
    private String detail;
    private String address;
}
