package com.letzgo.LetzgoBe.domain.schedule.repository;

import com.letzgo.LetzgoBe.domain.schedule.entity.SchedulePlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchedulePlaceRepository extends JpaRepository<SchedulePlace, Long> {
    List<SchedulePlace> findBySchedule_SchedulePk(Long schedulePk);
}
