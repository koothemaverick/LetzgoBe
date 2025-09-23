package com.letzgo.LetzgoBe.domain.account.auth.service;

import com.letzgo.LetzgoBe.domain.account.auth.dto.req.LoginRequest;
import com.letzgo.LetzgoBe.domain.account.auth.dto.res.LoginResponse;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.CurrentUserDto;

public interface AuthService {
    // 로그인
    LoginResponse login(LoginRequest loginRequest, boolean isSocialLogin);

    // 로그아웃
    void logout(CurrentUserDto loginUser);

    // accessToken 재발급
    LoginResponse refreshToken(String refreshToken, CurrentUserDto loginUser);
}
