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
    int sukbakPrice;
    int daesilPrice;
    int rating;
    String imagePath;
}
