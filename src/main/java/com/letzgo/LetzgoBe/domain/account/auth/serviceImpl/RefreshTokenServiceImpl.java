package com.letzgo.LetzgoBe.domain.account.auth.serviceImpl;

import com.letzgo.LetzgoBe.domain.account.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final StringRedisTemplate stringRedisTemplate;

    // Refresh Token 저장
    public void saveRefreshToken(String userId, String refreshToken, long duration) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set(userId, refreshToken, duration, TimeUnit.MILLISECONDS);
    }

    // Refresh Token 조회
    public String getRefreshToken(String userId) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        return ops.get(userId);
    }

    // Refresh Token 삭제
    public void deleteRefreshToken(String userId) {
        stringRedisTemplate.delete(userId);
    }
}