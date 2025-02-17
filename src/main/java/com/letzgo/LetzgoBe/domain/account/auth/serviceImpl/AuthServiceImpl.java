package com.letzgo.LetzgoBe.domain.account.auth.serviceImpl;

import com.letzgo.LetzgoBe.domain.account.auth.dto.req.LoginForm;
import com.letzgo.LetzgoBe.domain.account.auth.dto.req.RefreshToken;
import com.letzgo.LetzgoBe.domain.account.auth.dto.res.Auth;
import com.letzgo.LetzgoBe.domain.account.auth.security.JwtTokenProvider;
import com.letzgo.LetzgoBe.domain.account.auth.service.AuthService;
import com.letzgo.LetzgoBe.domain.account.auth.service.RefreshTokenService;
import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import com.letzgo.LetzgoBe.domain.account.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Value("${custom.accessToken.expiration}")
    private long accessTokenExpiration;

    @Value("${custom.refreshToken.expiration}")
    private long refreshTokenExpiration;

    // 로그인
    @Override
    @Transactional
    public Auth login(LoginForm loginForm) {
        User user = userRepository.findByEmail(loginForm.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!BCrypt.checkpw(loginForm.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtTokenProvider.generateToken(user.getEmail(), accessTokenExpiration);
        String refreshToken = jwtTokenProvider.generateToken(user.getEmail(), refreshTokenExpiration);

        // Refresh Token을 Redis에 저장
        refreshTokenService.saveRefreshToken(user.getId().toString(), refreshToken, refreshTokenExpiration);

        return new Auth(accessToken, refreshToken);
    }

    // 로그아웃
    @Override
    public void logout(String userId) {
        // Redis에서 Refresh Token 삭제
        refreshTokenService.deleteRefreshToken(userId);
    }

    // accessToken 재발급
    @Override
    @Transactional
    public Auth refreshToken(RefreshToken refreshTokenRequest) {
        String storedRefreshToken = refreshTokenService.getRefreshToken(refreshTokenRequest.getRefreshToken());

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshTokenRequest.getRefreshToken())) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }

        if (!jwtTokenProvider.validateToken(storedRefreshToken)) {
            throw new RuntimeException("리프레시 토큰이 만료되었습니다.");
        }

        String email = jwtTokenProvider.getEmailFromToken(storedRefreshToken);
        String newAccessToken = jwtTokenProvider.generateToken(email, accessTokenExpiration);

        return new Auth(newAccessToken, storedRefreshToken);
    }
}
