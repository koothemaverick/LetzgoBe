package com.letzgo.LetzgoBe.domain.account.auth.service;

import com.letzgo.LetzgoBe.domain.account.auth.dto.req.LoginForm;
import com.letzgo.LetzgoBe.domain.account.auth.dto.res.Auth;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;

public interface AuthService {
    // 로그인
    Auth login(LoginForm loginForm);

    // 로그아웃
    void logout(LoginUserDto loginUser);

    // accessToken 재발급
    Auth refreshToken(String refreshToken, LoginUserDto loginUser);
}
