package com.letzgo.LetzgoBe.domain.dataFetcher.dto.res;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
public class RestaurantResponse {
    String name;
    String region;
    String location;
    float rating;
    String category;
    String imagePath;
    double lat;
    double lng;
}
