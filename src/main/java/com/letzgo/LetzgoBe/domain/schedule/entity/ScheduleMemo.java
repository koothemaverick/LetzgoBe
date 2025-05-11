package com.letzgo.LetzgoBe.domain.schedule.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ScheduleMemo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleMemoPk;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_place_pk")
    private SchedulePlace schedulePlace;

    @Column(length = 500)
    private String content;
}