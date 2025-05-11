package com.letzgo.LetzgoBe.domain.schedule.service;

import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.account.member.repository.MemberRepository;
import com.letzgo.LetzgoBe.domain.schedule.dto.ScheduleDto;
import com.letzgo.LetzgoBe.domain.schedule.entity.Schedule;
import com.letzgo.LetzgoBe.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;

    /** 일정 생성 */
    public Schedule createSchedule(ScheduleDto dto) {
        Member member = memberRepository.findById(dto.getHostAccountPk())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Schedule schedule = new Schedule();
        schedule.setHostAccount(member);
        schedule.setRegion(dto.getRegion());
        schedule.setTitle(dto.getTitle());
        schedule.setStartDate(dto.getStartDate());
        schedule.setEndDate(dto.getEndDate());
        return scheduleRepository.save(schedule);
    }

    /** 일정 전체 조회 */
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    /** 일정 단건 조회 */
    public Optional<Schedule> getSchedule(Long schedulePk) {
        return scheduleRepository.findById(schedulePk);
    }

    /** 일정 삭제 */
    public void deleteSchedule(Long schedulePk) {
        scheduleRepository.deleteById(schedulePk);
    }
}
