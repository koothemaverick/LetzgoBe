package com.letzgo.LetzgoBe.domain.account.auth.controller;

import com.letzgo.LetzgoBe.domain.account.auth.dto.req.LoginRequest;
import com.letzgo.LetzgoBe.domain.account.auth.dto.res.LoginResponse;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.CurrentUser;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.account.auth.service.AuthService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest-api/v1/auth")
@RequiredArgsConstructor
public class ApiV1AuthController {
    private final AuthService authService;

    // 로그인
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ApiResponse.success(authService.login(loginRequest, false));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@CurrentUser CurrentUserDto loginUser) {
        authService.logout(loginUser);
        return ApiResponse.success();
    }

    // accessToken 재발급
    @GetMapping("/refresh-token")
    public ApiResponse<LoginResponse> refreshToken(@RequestHeader("Authorization") String refreshToken, @CurrentUser CurrentUserDto loginUser) {
        return ApiResponse.success(authService.refreshToken(refreshToken, loginUser));
    }
}
