package com.letzgo.LetzgoBe.domain.account.member.service;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.member.dto.req.MemberForm;
import com.letzgo.LetzgoBe.domain.account.member.dto.res.MemberInfo;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;

public interface MemberService {
    // 회원가입
    Member signup(MemberForm memberForm);

    // 회원정보 조회
    MemberInfo getMemberInfo(LoginUserDto loginUser);

    // 회원정보 수정
    void updateMember(MemberForm memberForm, LoginUserDto loginUser);

    // 회원탈퇴
    void deleteMember(LoginUserDto loginUser);
}
