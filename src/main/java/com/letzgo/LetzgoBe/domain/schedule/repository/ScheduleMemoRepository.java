package com.letzgo.LetzgoBe.domain.schedule.repository;

import com.letzgo.LetzgoBe.domain.schedule.entity.ScheduleMemo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleMemoRepository extends JpaRepository<ScheduleMemo, Long> {
    List<ScheduleMemo> findBySchedule_SchedulePk(Long schedulePk);
}
