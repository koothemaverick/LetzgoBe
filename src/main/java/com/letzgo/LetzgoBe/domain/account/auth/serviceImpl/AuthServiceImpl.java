package com.letzgo.LetzgoBe.domain.account.auth.serviceImpl;

import com.letzgo.LetzgoBe.domain.account.auth.dto.req.LoginForm;
import com.letzgo.LetzgoBe.domain.account.auth.dto.res.Auth;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.auth.security.JwtTokenProvider;
import com.letzgo.LetzgoBe.domain.account.auth.service.AuthService;
import com.letzgo.LetzgoBe.domain.account.auth.service.RefreshTokenService;
import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import com.letzgo.LetzgoBe.domain.account.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

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
        if (!passwordEncoder.matches(loginForm.getPassword(), user.getPassword())) {
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
    @Transactional
    public void logout(LoginUserDto loginUser) {
        // Redis에서 Refresh Token 삭제
        refreshTokenService.deleteRefreshToken(loginUser.getId().toString());
    }

    // accessToken 재발급
    @Override
    @Transactional
    public Auth refreshToken(String refreshToken, LoginUserDto loginUser) {
        // "Bearer "가 붙어있다면 제거
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        String storedRefreshToken = refreshTokenService.getRefreshToken(loginUser.getId().toString());
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
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
