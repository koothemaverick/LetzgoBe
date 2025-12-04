package com.letzgo.LetzgoBe.domain.account.auth.controller;

import com.letzgo.LetzgoBe.domain.account.auth.dto.req.LoginRequest;
import com.letzgo.LetzgoBe.domain.account.auth.dto.res.LoginResponse;
import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUser;
import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.account.auth.service.AuthService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest-api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = """
일반 인증 API

소셜 로그인 플로우:
1. 프론트에서 /oauth2/authorization/{provider} 로 브라우저 리다이렉트
   - provider: google, kakao, naver
2. 로그인 완료 후 백엔드에서 JWT 발급
""")
public class ApiV1AuthController {
    private final AuthService authService;

    // 로그인
    @Operation(summary = "로그인", description = "이메일로 로그인합니다.")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ApiResponse.success(authService.login(loginRequest, false));
    }

    // 로그아웃
    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자가 로그아웃합니다.")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@CurrentUser CurrentUserDto currentUser) {
        authService.logout(currentUser);
        return ApiResponse.success();
    }

    // accessToken 재발급
    @Operation(summary = "accessToken 재발급", description = "refresh_token을 사용하여 access_token을 재발급합니다.")
    @GetMapping("/refresh-token")
    public ApiResponse<LoginResponse> refreshToken(@RequestHeader("Authorization") String refreshToken, @CurrentUser CurrentUserDto currentUser) {
        return ApiResponse.success(authService.refreshToken(refreshToken, currentUser));
    }
}
