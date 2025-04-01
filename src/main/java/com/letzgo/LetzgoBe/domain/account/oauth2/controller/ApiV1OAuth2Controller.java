package com.letzgo.LetzgoBe.domain.account.oauth2.controller;

import com.letzgo.LetzgoBe.domain.account.auth.dto.req.LoginForm;
import com.letzgo.LetzgoBe.domain.account.auth.dto.res.Auth;
import com.letzgo.LetzgoBe.domain.account.auth.service.AuthService;
import com.letzgo.LetzgoBe.domain.account.member.dto.req.MemberForm;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.account.member.repository.MemberRepository;
import com.letzgo.LetzgoBe.domain.account.member.service.MemberService;
import com.letzgo.LetzgoBe.domain.account.oauth2.service.OAuth2Service;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/rest-api/v1/oauth2")
@RequiredArgsConstructor
public class ApiV1OAuth2Controller {
    private final AuthService authService;
    private final OAuth2Service oAuth2Service;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    // 소셜 로그인 리디렉션 URL
    @GetMapping("/redirect-url/{provider}")
    public String redirectToProvider(@PathVariable("provider") String provider) {
        String authUrl = oAuth2Service.getAuthUrl(provider);
        return "redirect:" + authUrl;
    }

    // 소셜 로그인
    @GetMapping("/{provider}")
    public ApiResponse<Auth> socialLogin(@PathVariable("provider") String provider, @RequestParam(value = "code") String code) {
        Map<String, String> socialUser = oAuth2Service.getUserInfo(provider, code);
        String email = socialUser.get("email");
        String name = socialUser.get("name");
        // 이미 존재하는 회원이면 로그인 처리
        if (memberRepository.existsByEmail(email)) {
            LoginForm socialLoginForm = LoginForm.builder().email(email).build();
            return ApiResponse.of(authService.login(socialLoginForm, true));
        }
        // 회원가입
        MemberForm memberForm = MemberForm.builder()
                .email(email)
                .name(name)
                .nickname(name)  // 소셜 로그인에서는 닉네임을 이름으로 설정
                .phone(null)      // 소셜 로그인에는 전화번호가 없으므로 null로 설정
                .gender(null)     // 성별이 없을 경우 null
                .birthday(null)   // 생일이 없을 경우 null
                .password("")   // 비밀번호는 필요없음
                .build();
        Member newMember = memberService.signup(memberForm);
        // 회원가입 후 로그인 처리
        LoginForm socialLoginForm = LoginForm.builder().email(newMember.getEmail()).build();
        return ApiResponse.of(authService.login(socialLoginForm, true));
    }
}
