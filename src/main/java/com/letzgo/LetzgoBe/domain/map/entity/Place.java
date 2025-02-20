package com.letzgo.LetzgoBe.domain.map.entity;

import com.letzgo.LetzgoBe.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
public class Place extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String detail;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;
}
