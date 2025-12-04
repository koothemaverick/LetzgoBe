package com.letzgo.LetzgoBe.domain.account.member.controller;

import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUser;
import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.account.member.dto.req.MemberRequest;
import com.letzgo.LetzgoBe.domain.account.member.dto.res.DetailMemberResponse;
import com.letzgo.LetzgoBe.domain.account.member.dto.res.MemberResponse;
import com.letzgo.LetzgoBe.domain.account.member.entity.MemberPage;
import com.letzgo.LetzgoBe.domain.account.member.service.MemberService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/rest-api/v1/member")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 API")
public class ApiV1MemberController {
    private final MemberService memberService;

    // 회원가입
    @PostMapping
    @Operation(summary = "회원가입", description = "회원정보 입력 후 회원가입합니다.")
    public ApiResponse<Void> signup(@RequestBody @Valid MemberRequest memberRequest) {
        memberService.signup(memberRequest);
        return ApiResponse.success();
    }

    // 본인 회원정보 조회
    @GetMapping
    @Operation(summary = "본인 회원정보 조회", description = "현재 로그인된 사용자의 회원정보를 조회합니다.")
    public ApiResponse<MemberResponse> getMyInfo(@CurrentUser CurrentUserDto currentUser) {
        return ApiResponse.success(memberService.getMyInfo(currentUser));
    }

    // 본인 상세회원정보 조회
    @GetMapping("/detail")
    @Operation(summary = "본인 상세회원정보 조회", description = "현재 로그인된 사용자의 상세회원정보를 조회합니다.")
    public ApiResponse<DetailMemberResponse> getMyDetailInfo(@CurrentUser CurrentUserDto currentUser) {
        return ApiResponse.success(memberService.getMyDetailInfo(currentUser));
    }

    // 다른 멤버의 회원정보 조회
    @GetMapping("/{memberId}")
    @Operation(summary = "다른 사용자의 회원정보 조회", description = "다른 사용자의 회원정보를 조회합니다.")
    public ApiResponse<MemberResponse> getMemberInfo(@PathVariable("memberId") Long memberId) {
        return ApiResponse.success(memberService.getMemberInfo(memberId));
    }

    // 다른 멤버의 상세회원정보 조회
    @GetMapping("/detail/{memberId}")
    @Operation(summary = "다른 사용자의 상세회원정보 조회", description = "다른 사용자의 상세회원정보를 조회합니다.")
    public ApiResponse<DetailMemberResponse> getDetailMemberInfo(@PathVariable("memberId") Long memberId) {
        return ApiResponse.success(memberService.getDetailMemberInfo(memberId));
    }

    // 회원정보 수정
    @PatchMapping
    @Operation(summary = "회원정보 수정", description = "현재 로그인된 사용자의 회원정보를 수정합니다.")
    public ApiResponse<Void> updateMemberInfo(@RequestPart(value = "memberForm") @Valid MemberRequest memberRequest,
                                                @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
                                                @CurrentUser CurrentUserDto currentUser) {
        memberService.updateMember(memberRequest, imageFile, currentUser);
        return ApiResponse.success();
    }

    // 회원탈퇴
    @DeleteMapping
    @Operation(summary = "회원탈퇴", description = "현재 로그인된 사용자가 회원탈퇴합니다.")
    public ApiResponse<Void> deleteMember(@CurrentUser CurrentUserDto currentUser) {
        memberService.deleteMember(currentUser);
        return ApiResponse.success();
    }

    // 회원 검색하기
    @GetMapping("/search")
    @Operation(summary = "회원 검색", description = "현재 로그인된 사용자가 회원을 검색합니다.(기본설정: page=0, size=15)")
    public ApiResponse<List<MemberResponse>> searchMemberInfo(@ModelAttribute MemberPage request, @RequestParam(value = "keyword") String keyword) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(memberService.searchMemberInfo(pageable, keyword));
    }

    // 팔로우 요청하기
    @PostMapping("/follow/{memberId}")
    @Operation(summary = "팔로우 요청", description = "현재 로그인된 사용자가 팔로우를 요청합니다.")
    public ApiResponse<Void> followReq(@PathVariable("memberId") Long memberId, @CurrentUser CurrentUserDto currentUser) {
        memberService.followReq(memberId, currentUser);
        return ApiResponse.success();
    }

    // 팔로우 요청 취소하기
    @DeleteMapping("/follow/{memberId}")
    @Operation(summary = "팔로우 요청 취소", description = "현재 로그인된 사용자가 팔로우 요청을 취소합니다.")
    public ApiResponse<Void> cancelFollowReq(@PathVariable("memberId") Long memberId, @CurrentUser CurrentUserDto currentUser) {
        memberService.cancelFollowReq(memberId, currentUser);
        return ApiResponse.success();
    }

    // 팔로우 요청 수락하기
    @PostMapping("/followReq/{memberId}")
    @Operation(summary = "팔로우 요청 수락", description = "현재 로그인된 사용자가 팔로우 요청을 수락합니다.")
    public ApiResponse<Void> acceptFollowReq(@PathVariable("memberId") Long memberId, @CurrentUser CurrentUserDto currentUser) {
        memberService.acceptFollowReq(memberId, currentUser);
        return ApiResponse.success();
    }

    // 팔로우 요청 거절하기
    @DeleteMapping("/followReq/{memberId}")
    @Operation(summary = "팔로우 요청 거절", description = "현재 로그인된 사용자가 팔로우 요청을 거절합니다.")
    public ApiResponse<Void> refuseFollowReq(@PathVariable("memberId") Long memberId, @CurrentUser CurrentUserDto currentUser) {
        memberService.refuseFollowReq(memberId, currentUser);
        return ApiResponse.success();
    }

    // 팔로우 취소하기
    @DeleteMapping("/followMember/{memberId}")
    @Operation(summary = "팔로우 취소", description = "현재 로그인된 사용자가 팔로우를 취소합니다.")
    public ApiResponse<Void> cancelFollow(@PathVariable("memberId") Long memberId, @CurrentUser CurrentUserDto currentUser) {
        memberService.cancelFollow(memberId, currentUser);
        return ApiResponse.success();
    }

    // 팔로워 목록에서 해당 유저 삭제하기
    @DeleteMapping("/followed/{memberId}")
    @Operation(summary = "팔로워 목록에서 삭제", description = "현재 로그인된 사용자가 팔로워 목록에서 삭제합니다.")
    public ApiResponse<Void> removeFollowed(@PathVariable("memberId") Long memberId, @CurrentUser CurrentUserDto currentUser) {
        memberService.removeFollowed(memberId, currentUser);
        return ApiResponse.success();
    }
}
