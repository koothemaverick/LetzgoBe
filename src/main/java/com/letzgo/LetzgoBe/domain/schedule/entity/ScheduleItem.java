package com.letzgo.LetzgoBe.domain.schedule.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ScheduleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleItemPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_pk")
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_types_pk")
    private ItemType itemType;

    private Long itemPk;     // 장소 or 메모의 PK
    private int orderIndex;  // 순서 그룹
    private int sequence;    // 실제 시간 순서
}