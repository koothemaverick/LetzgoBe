package com.letzgo.LetzgoBe.domain.account.auth.serviceImpl;

import com.letzgo.LetzgoBe.domain.account.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RedisTemplate<String, Object> redisTemplate;

    // Refresh Token 저장
    public void saveRefreshToken(String userId, String refreshToken, long duration) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(userId, refreshToken, duration, TimeUnit.MILLISECONDS);
    }

    // Refresh Token 조회
    public String getRefreshToken(String userId) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Object value = ops.get(userId);
        return value != null ? value.toString() : null;
    }

    // Refresh Token 삭제
    public void deleteRefreshToken(String token) {
        redisTemplate.delete(token);
    }
}