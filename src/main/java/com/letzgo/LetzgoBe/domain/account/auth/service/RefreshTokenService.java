package com.letzgo.LetzgoBe.domain.account.auth.service;

public interface RefreshTokenService {
    // Refresh Token 저장
    void saveRefreshToken(String userId, String refreshToken, long duration);

    // Refresh Token 조회
    String getRefreshToken(String userId);

    // Refresh Token 삭제
    void deleteRefreshToken(String userId);
}
