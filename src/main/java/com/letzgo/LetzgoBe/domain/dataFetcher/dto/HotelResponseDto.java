package com.letzgo.LetzgoBe.domain.dataFetcher.dto;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
public class HotelResponseDto {
    String region;
    String name;
    String location;
    Integer sukbakPrice;
    Integer daesilPrice;
    float rating;
    String imagePath;
    Double lat;
    Double lng;

}
