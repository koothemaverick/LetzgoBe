package com.letzgo.LetzgoBe.domain.account.auth.service;

import com.letzgo.LetzgoBe.domain.account.auth.dto.req.LoginForm;
import com.letzgo.LetzgoBe.domain.account.auth.dto.req.RefreshToken;
import com.letzgo.LetzgoBe.domain.account.auth.dto.res.Auth;

public interface AuthService {
    // 로그인
    Auth login(LoginForm loginForm);

    // 로그아웃
    void logout(String token);

    // accessToken 재발급
    Auth refreshToken(RefreshToken refreshToken);
}
