package com.letzgo.LetzgoBe.domain.dataFetcher.dto;

import jakarta.persistence.Column;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
public class RestaurantResponseDto {
    String name;
    String region;
    String location;
    float rating;
    String category;
    String imagePath;
    double lat;
    double lng;
}
