package com.letzgo.LetzgoBe.domain.dataFetcher.entity;

import com.letzgo.LetzgoBe.domain.dataFetcher.dto.res.HotelResponse;
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
public class Hotel extends BaseEntity {
    @Column
    String name;

    @Column
    String region; //경기도, 대구, 제주도 같은 지역명

    @Column
    String location; // 세부 주소

    @Column(name = "sukbak_price")
    Integer sukbakPrice;

    @Column(name = "daesil_price")
    Integer daesilPrice;

    @Column
    float rating;

    @Column(name = "image_path", length = 1000)
    String imagePath;

    @Column //좌표
    double lat;

    @Column
    double lng;

    public HotelResponse toDto() {
        HotelResponse dto = HotelResponse.builder()
                .name(this.name)
                .region(this.region)
                .location(this.location)
                .sukbakPrice(this.sukbakPrice)
                .daesilPrice(this.daesilPrice)
                .rating(this.rating)
                .imagePath(this.imagePath)
                .lat(this.lat)
                .lng(this.lng)
                .build();
        return dto;
    }
}
