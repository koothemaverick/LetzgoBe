package com.letzgo.LetzgoBe.domain.account.member.controller;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUser;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.member.dto.req.MemberForm;
import com.letzgo.LetzgoBe.domain.account.member.dto.res.MemberInfo;
import com.letzgo.LetzgoBe.domain.account.member.service.MemberService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest-api/v1/member")
@RequiredArgsConstructor
public class ApiV1MemberController {
    private final MemberService memberService;

    // 회원가입
    @PostMapping
    public ApiResponse<String> signup(@RequestBody MemberForm memberForm) {
        memberService.signup(memberForm);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 본인 회원정보 조회 [회원 권한]
    @GetMapping
    public ApiResponse<MemberInfo> getMemberInfo(@LoginUser LoginUserDto loginUser) {
        return ApiResponse.of(memberService.getMemberInfo(loginUser));
    }

    // 다른 멤버의 회원정보 조회 [회원 권한]

    // 회원정보 수정 [회원 권한]
    @PutMapping
    public ApiResponse<String> updateMemberInfo(@RequestBody MemberForm memberForm, @LoginUser LoginUserDto loginUser) {
        memberService.updateMember(memberForm, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 회원탈퇴 [회원 권한]
    @DeleteMapping
    public ApiResponse<String> deleteMember(@LoginUser LoginUserDto loginUser) {
        memberService.deleteMember(loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 회원 검색하기 [회원 권한]

    // 팔로우 신청하기 [회원 권한]

    // 팔로우 목록 가져오기 [회원 권한]

    // 팔로우 취소하기 [회원 권한]

    // 팔로우 신청 수락하기 [회원 권한]

    // 팔로우 신청 거절하기 [회원 권한]

    // 팔로워 목록 가져오기 [회원 권한]

    // 팔로워 목록에서 해당 유저 삭제하기 [회원 권한]
}
