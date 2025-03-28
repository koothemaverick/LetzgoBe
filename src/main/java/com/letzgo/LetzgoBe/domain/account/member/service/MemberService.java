package com.letzgo.LetzgoBe.domain.account.member.service;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.member.dto.req.MemberForm;
import com.letzgo.LetzgoBe.domain.account.member.dto.res.MemberInfo;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;

public interface MemberService {
    // 회원가입
    Member signup(MemberForm memberForm);

    // 회원정보 조회
    MemberInfo getMyInfo(LoginUserDto loginUser);

    // 다른 멤버의 회원정보 조회
    MemberInfo getMemberInfo(Long memberId, LoginUserDto loginUser);

    // 회원정보 수정
    void updateMember(MemberForm memberForm, LoginUserDto loginUser);

    // 회원탈퇴
    void deleteMember(LoginUserDto loginUser);

    // 회원 검색하기


    // 팔로우 신청하기


    // 팔로우 취소하기


    // 팔로우 목록 가져오기


    // 팔로우 신청 수락하기


    // 팔로우 신청 거절하기


    // 팔로워 목록 가져오기


    // 팔로워 목록에서 해당 유저 삭제하기

}
