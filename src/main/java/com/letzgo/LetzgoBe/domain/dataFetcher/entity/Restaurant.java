package com.letzgo.LetzgoBe.domain.dataFetcher.entity;

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
public class Restaurant extends BaseEntity {
    @Column
    String name;

    @Column
    String location;

    @Column
    float rating;

    @Column
    String category;

    @Column(name = "image_path", length = 1000)
    String imagePath;
}
