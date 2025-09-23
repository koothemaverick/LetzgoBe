package com.letzgo.LetzgoBe.domain.account.member.service;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.account.member.dto.req.MemberRequest;
import com.letzgo.LetzgoBe.domain.account.member.dto.res.DetailMemberResponse;
import com.letzgo.LetzgoBe.domain.account.member.dto.res.MemberResponse;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.global.common.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    // 회원가입
    Member signup(MemberRequest memberRequest);

    // 본인 회원정보 조회
    MemberResponse getMyInfo(CurrentUserDto loginUser);

    // 본인 상세회원정보 조회
    DetailMemberResponse getMyDetailInfo(CurrentUserDto loginUser);

    // 다른 멤버의 회원정보 조회
    MemberResponse getMemberInfo(Long memberId);

    // 다른 멤버의 상세회원정보 조회
    DetailMemberResponse getDetailMemberInfo(Long memberId);

    // 회원정보 수정
    void updateMember(MemberRequest memberRequest, MultipartFile imageFile, CurrentUserDto loginUser);

    // 회원탈퇴
    void deleteMember(CurrentUserDto loginUser);

    // 회원 검색하기
    PageResponse<MemberResponse> searchMemberInfo(Pageable pageable, String keyword);

    // 팔로우 요청하기
    void followReq(Long memberId, CurrentUserDto loginUser);

    // 팔로우 요청 취소하기
    void cancelFollowReq(Long memberId, CurrentUserDto loginUser);

    // 팔로우 요청 수락하기
    void acceptFollowReq(Long memberId, CurrentUserDto loginUser);

    // 팔로우 요청 거절하기
    void refuseFollowReq(Long memberId, CurrentUserDto loginUser);

    // 팔로우 취소하기
    void cancelFollow(Long memberId, CurrentUserDto loginUser);

    // 팔로워 목록에서 해당 유저 삭제하기
    void removeFollowed(Long memberId, CurrentUserDto loginUser);
}
