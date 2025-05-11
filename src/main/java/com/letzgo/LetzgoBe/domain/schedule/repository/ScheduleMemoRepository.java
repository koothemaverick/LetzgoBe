package com.letzgo.LetzgoBe.domain.schedule.repository;

import com.letzgo.LetzgoBe.domain.schedule.entity.ScheduleMemo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleMemoRepository extends JpaRepository<ScheduleMemo, Long> {
    Optional<ScheduleMemo> findBySchedulePlace_SchedulePlacePk(Long schedulePlacePk);
}
