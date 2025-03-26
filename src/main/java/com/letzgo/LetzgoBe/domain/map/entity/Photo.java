package com.letzgo.LetzgoBe.domain.map.entity;

import com.letzgo.LetzgoBe.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Photo extends BaseEntity {
    @Column(nullable = false)
    String upload_name;

    @Column(nullable = false)
    String store_dir;
}
