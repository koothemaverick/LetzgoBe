package com.letzgo.LetzgoBe.domain.schedule.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ItemType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemTypesPk;

    private String typeName; // 장소, 메모 등
}