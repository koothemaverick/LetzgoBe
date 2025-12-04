package com.letzgo.LetzgoBe.global.oauth;

import com.letzgo.LetzgoBe.domain.account.auth.dto.req.LoginRequest;
import com.letzgo.LetzgoBe.domain.account.auth.dto.res.LoginResponse;
import com.letzgo.LetzgoBe.domain.account.auth.service.AuthService;
import com.letzgo.LetzgoBe.domain.account.member.dto.req.MemberRequest;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.account.member.repository.MemberRepository;
import com.letzgo.LetzgoBe.domain.account.member.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomOAuth2Handler implements AuthenticationSuccessHandler {
    private final AuthService authService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    @Value("${frontend.oauth-redirect}")
    private String frontendRedirect;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String provider = oauthToken.getAuthorizedClientRegistrationId(); // google, kakao, naver
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 1) provider별로 email, name 파싱
        String email;
        String name;
        switch (provider) {
            case "google" -> {
                email = (String) attributes.get("email");
                name = (String) attributes.getOrDefault("name", "GoogleUser");
            }
            case "kakao" -> {
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                if (kakaoAccount != null) {
                    email = (String) kakaoAccount.get("email");
                    if (email == null) {
                        email = "kakao_" + attributes.get("id") + "@example.com";
                    }
                    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                    name = profile != null
                            ? (String) profile.getOrDefault("nickname", "KakaoUser")
                            : "KakaoUser";
                } else {
                    email = "kakao_unknown@example.com";
                    name = "KakaoUser";
                }
            }
            case "naver" -> {
                Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
                if (naverResponse != null) {
                    email = (String) naverResponse.getOrDefault("email", "unknown@naver.com");
                    name = (String) naverResponse.getOrDefault("name", "NaverUser");
                } else {
                    throw new IllegalStateException("네이버 사용자 정보를 가져올 수 없습니다.");
                }
            }
            default -> throw new IllegalArgumentException("지원되지 않는 OAuth2 제공자: " + provider);
        }

        // 2) 로그인 or 회원가입 (기존 컨트롤러 코드 그대로 가져오기)
        LoginRequest socialLoginRequest;
        if (memberRepository.existsByEmail(email)) {
            socialLoginRequest = LoginRequest.builder()
                    .email(email)
                    .build();
        } else {
            MemberRequest memberRequest = MemberRequest.builder()
                    .email(email)
                    .name(name)
                    .nickname(name)
                    .phone(null)
                    .gender(null)
                    .birthday(null)
                    .password("") // 필요하면 랜덤 값으로 변경
                    .build();
            Member newMember = memberService.signup(memberRequest);
            socialLoginRequest = LoginRequest.builder()
                    .email(newMember.getEmail())
                    .build();
        }
        LoginResponse loginResponse = authService.login(socialLoginRequest, true);
        String accessToken = loginResponse.getAccessToken();
        String refreshToken = loginResponse.getRefreshToken();

        // 3) 프론트 리다이렉트 (기존과 동일)
        String redirectUrl = UriComponentsBuilder.fromUriString(frontendRedirect)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build()
                .toUriString();
        response.sendRedirect(redirectUrl);
    }
}
