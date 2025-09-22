package com.letzgo.LetzgoBe.domain.schedule.service;

import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.account.member.repository.MemberRepository;
import com.letzgo.LetzgoBe.domain.schedule.dto.res.ScheduleMemberResponse;
import com.letzgo.LetzgoBe.domain.schedule.entity.Schedule;
import com.letzgo.LetzgoBe.domain.schedule.entity.ScheduleMember;
import com.letzgo.LetzgoBe.domain.schedule.repository.ScheduleMemberRepository;
import com.letzgo.LetzgoBe.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleMemberService {

    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;
    private final ScheduleMemberRepository scheduleMemberRepository;

    @Transactional
    public void inviteMember(Long schedulePk, Long memberPk) {
        if (scheduleMemberRepository.existsBySchedule_SchedulePkAndMember_Id(schedulePk, memberPk)) {
            return;  // 중복 방지
        }
        Schedule schedule = scheduleRepository.findById(schedulePk).orElseThrow();
        Member member = memberRepository.findById(memberPk).orElseThrow();

        ScheduleMember scheduleMember = ScheduleMember.builder()
                .schedule(schedule)
                .member(member)
                .build();

        scheduleMemberRepository.save(scheduleMember);
    }

    @Transactional(readOnly = true)
    public List<ScheduleMemberResponse> getInvitedMembers(Long schedulePk) {
        return scheduleMemberRepository.findBySchedule_SchedulePk(schedulePk).stream()
                .map(sm -> {
                    ScheduleMemberResponse dto = new ScheduleMemberResponse();
                    dto.setMemberPk(sm.getMember().getId());
                    dto.setNickname(sm.getMember().getNickname());
                    dto.setProfileImageUrl(sm.getMember().getProfileImageUrl());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<ScheduleMemberResponse> getCandidateMembers(Long schedulePk, Long requesterPk) {
        Member loginMember = memberRepository.findById(requesterPk).orElseThrow();

        Set<Long> invitedIds = scheduleMemberRepository.findBySchedule_SchedulePk(schedulePk)
                .stream()
                .map(sm -> sm.getMember().getId())
                .collect(Collectors.toSet());

        return memberRepository.findAll().stream()
                .filter(m -> !m.getId().equals(loginMember.getId()))
                .filter(m -> !invitedIds.contains(m.getId()))
                .map(m -> {
                    ScheduleMemberResponse dto = new ScheduleMemberResponse();
                    dto.setMemberPk(m.getId());
                    dto.setNickname(m.getNickname());
                    dto.setProfileImageUrl(m.getProfileImageUrl());
                    return dto;
                })
                .toList();
    }
}
