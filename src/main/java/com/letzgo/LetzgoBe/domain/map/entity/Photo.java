package com.letzgo.LetzgoBe.domain.map.entity;

import com.letzgo.LetzgoBe.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Getter
public class Photo extends BaseEntity {
    @Column(nullable = false)
    String upload_name;

    @Column(nullable = false)
    String store_dir;
}
