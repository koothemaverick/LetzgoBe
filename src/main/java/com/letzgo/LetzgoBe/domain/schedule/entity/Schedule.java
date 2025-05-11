package com.letzgo.LetzgoBe.domain.schedule.entity;

import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long schedulePk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_account_pk", nullable = false)
    private Member hostAccount;

    private String region;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SchedulePlace> places = new ArrayList<>();

}
