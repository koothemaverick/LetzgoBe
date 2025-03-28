package com.letzgo.LetzgoBe.domain.map.entity;

import com.letzgo.LetzgoBe.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Place extends BaseEntity {
    @Column(name = "place_id", nullable = false)
    private String placeId;
}
