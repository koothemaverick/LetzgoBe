package com.letzgo.LetzgoBe.domain.hotel.entity;

import com.letzgo.LetzgoBe.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
public class Hotel extends BaseEntity {
    @Column
    String name;

    @Column
    String location;

    @Column(name = "sukbak_price")
    int sukbakPrice;

    @Column(name = "daesil_price")
    int daesilPrice;

    @Column
    int rating;

    @Column(name = "image_path")
    String imagePath;
}
