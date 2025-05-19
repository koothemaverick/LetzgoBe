package com.letzgo.LetzgoBe.domain.schedule.repository;

import com.letzgo.LetzgoBe.domain.schedule.entity.SchedulePlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchedulePlaceRepository extends JpaRepository<SchedulePlace, Long> {
    List<SchedulePlace> findBySchedule_SchedulePk(Long schedulePk);
    /** 특정 스케줄의 day별 장소들을 sequence 오름차순으로 가져오기 */
    List<SchedulePlace> findBySchedule_SchedulePkAndOrderIndexOrderBySequence(Long schedulePk, int orderIndex);
}
