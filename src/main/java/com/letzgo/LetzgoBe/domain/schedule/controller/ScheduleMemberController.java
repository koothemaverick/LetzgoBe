package com.letzgo.LetzgoBe.domain.schedule.controller;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUser;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.schedule.dto.ScheduleMemberDto;
import com.letzgo.LetzgoBe.domain.schedule.service.ScheduleMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules/{schedulePk}/members")
public class ScheduleMemberController {

    private final ScheduleMemberService scheduleMemberService;

    /** 일행 초대 */
    @PostMapping("/{memberPk}")
    public ResponseEntity<Void> invite(@PathVariable("schedulePk") Long schedulePk, @PathVariable("memberPk") Long memberPk) {
        scheduleMemberService.inviteMember(schedulePk, memberPk);
        return ResponseEntity.ok().build();
    }

    /** 초대한 일행 목록 조회 */
    @GetMapping
    public ResponseEntity<List<ScheduleMemberDto>> getInvited(@PathVariable("schedulePk") Long schedulePk) {
        return ResponseEntity.ok(scheduleMemberService.getInvitedMembers(schedulePk));
    }
    @GetMapping("/candidates")
    public ResponseEntity<List<ScheduleMemberDto>> getCandidateMembers(
            @PathVariable("schedulePk") Long schedulePk,
            @RequestParam("requesterPk") Long requesterPk
    ) {
        List<ScheduleMemberDto> members = scheduleMemberService.getCandidateMembers(schedulePk, requesterPk);
        return ResponseEntity.ok(members);
    }
}
