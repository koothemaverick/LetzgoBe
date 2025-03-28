package com.letzgo.LetzgoBe.domain.account.auth.controller;

import com.letzgo.LetzgoBe.domain.account.auth.dto.req.LoginForm;
import com.letzgo.LetzgoBe.domain.account.auth.dto.res.Auth;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUser;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.auth.service.AuthService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest-api/v1/auth")
@RequiredArgsConstructor
public class ApiV1AuthController {
    private final AuthService authService;

    // 로그인
    @PostMapping("/login")
    public ApiResponse<Auth> login(@RequestBody @Valid LoginForm loginForm) {
        return ApiResponse.of(authService.login(loginForm));
    }

    // 로그아웃 [회원 권한]
    @PostMapping("/logout")
    public ApiResponse<String> logout(@LoginUser LoginUserDto loginUser) {
        authService.logout(loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // accessToken 재발급 [회원 권한]
    @GetMapping("/refresh-token")
    public ApiResponse<Auth> refreshToken(@RequestHeader("Authorization") String refreshToken, @LoginUser LoginUserDto loginUser) {
        return ApiResponse.of(authService.refreshToken(refreshToken, loginUser));
    }
}
