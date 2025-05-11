package com.letzgo.LetzgoBe.domain.schedule.repository;

import com.letzgo.LetzgoBe.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

}


