package com.letzgo.LetzgoBe.domain.dataFetcher.dto.res;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
public class HotelResponse {
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
