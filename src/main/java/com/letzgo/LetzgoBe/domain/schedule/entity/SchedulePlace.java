package com.letzgo.LetzgoBe.domain.schedule.entity;

import com.letzgo.LetzgoBe.domain.map.entity.Place;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SchedulePlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long schedulePlacePk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_pk")
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_pk")
    private Place place;

    private int orderIndex;
}