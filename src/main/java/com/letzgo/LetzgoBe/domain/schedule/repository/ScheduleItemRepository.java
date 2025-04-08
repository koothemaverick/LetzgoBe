package com.letzgo.LetzgoBe.domain.schedule.repository;

import com.letzgo.LetzgoBe.domain.schedule.entity.ScheduleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, Long> {
    List<ScheduleItem> findBySchedule_SchedulePk(Long schedulePk);
    List<ScheduleItem> findBySchedule_SchedulePkAndItemType_TypeName(Long schedulePk, String typeName);
}