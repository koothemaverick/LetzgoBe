package com.letzgo.LetzgoBe.domain.account.auth.controller;

import com.letzgo.LetzgoBe.domain.account.auth.dto.req.LoginForm;
import com.letzgo.LetzgoBe.domain.account.auth.dto.req.RefreshToken;
import com.letzgo.LetzgoBe.domain.account.auth.dto.res.Auth;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUser;
import com.letzgo.LetzgoBe.domain.account.auth.service.AuthService;
import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("letzgo/rest-api/v1/auth")
@RequiredArgsConstructor
public class ApiV1AuthController {
    private final AuthService authService;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Auth> login(@RequestBody LoginForm loginForm) {
        return ResponseEntity.ok(authService.login(loginForm));
    }

    // 로그아웃
    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token, @LoginUser User loginUser) {
        authService.logout(token, loginUser);
        return ResponseEntity.ok("로그아웃 성공");
    }

    // accessToken 재발급
    @GetMapping("/refresh-token")
    public ResponseEntity<Auth> refreshToken(@RequestBody RefreshToken refreshToken, @LoginUser User loginUser) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken, loginUser));
    }
}
