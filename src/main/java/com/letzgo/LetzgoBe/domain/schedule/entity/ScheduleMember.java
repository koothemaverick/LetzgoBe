package com.letzgo.LetzgoBe.domain.schedule.entity;

import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleMemberPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_pk")
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_pk")
    private Member member;
}
