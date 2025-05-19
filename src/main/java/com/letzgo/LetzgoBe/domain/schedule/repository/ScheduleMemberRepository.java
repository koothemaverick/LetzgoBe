package com.letzgo.LetzgoBe.domain.schedule.repository;

import com.letzgo.LetzgoBe.domain.schedule.entity.ScheduleMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleMemberRepository extends JpaRepository<ScheduleMember, Long> {
    List<ScheduleMember> findBySchedule_SchedulePk(Long schedulePk);

    boolean existsBySchedule_SchedulePkAndMember_Id(Long schedulePk, Long memberId);
}
