package com.letzgo.LetzgoBe.domain.dataFetcher.entity;

import com.letzgo.LetzgoBe.domain.dataFetcher.dto.RestaurantResponseDto;
import com.letzgo.LetzgoBe.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class Restaurant extends BaseEntity {
    @Column
    String name;

    @Column
    String region;

    @Column
    String location;

    @Column
    float rating;

    @Column
    String category;

    @Column(name = "image_path", length = 1000)
    String imagePath;

    @Column
    double lat;

    @Column
    double lng;

    public RestaurantResponseDto toDto() {
        RestaurantResponseDto dto = RestaurantResponseDto.builder()
                .name(this.name)
                .region(this.region)
                .location(this.location)
                .rating(this.rating)
                .category(this.category)
                .imagePath(this.imagePath)
                .lat(this.lat)
                .lng(this.lng)
                .build();
        return dto;
    }
}
