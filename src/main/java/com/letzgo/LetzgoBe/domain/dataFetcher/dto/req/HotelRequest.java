package com.letzgo.LetzgoBe.domain.dataFetcher.dto.req;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
public class HotelRequest {
    String region;
    String name;
    String location;
    Integer sukbakPrice;
    Integer daesilPrice;
    float rating;
    String imagePath;
}
