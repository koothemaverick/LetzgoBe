package com.letzgo.LetzgoBe.domain.hotel.dto;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
public class HotelDto {
    String name;
    String location;
    Integer sukbakPrice;
    Integer daesilPrice;
    float rating;
    String imagePath;
}
