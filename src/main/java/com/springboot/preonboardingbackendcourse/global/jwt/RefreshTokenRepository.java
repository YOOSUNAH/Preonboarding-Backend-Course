package com.springboot.preonboardingbackendcourse.global.jwt;


import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j(topic = "refreshTokenRepository")
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private static final String TOKEN_PREFIX = "token_";
    private final RedisTemplate<String, String> redisTemplate;

    private ValueOperations<String, String> getValueOperations() {
        return redisTemplate.opsForValue();
    }

    public void delete(final String subject) {
        redisTemplate.delete(TOKEN_PREFIX + subject);
    }

    public void save(final Long userId, final String refreshToken, Long expiration) {
        ValueOperations<String, String> valueOperations = getValueOperations();
        valueOperations.set(TOKEN_PREFIX + userId, refreshToken);
        redisTemplate.expire(TOKEN_PREFIX + userId, expiration, TimeUnit.SECONDS);
    }

    public boolean existsByUserId(Long userId) {
        ValueOperations<String, String> valueOperations = getValueOperations();
        return Boolean.TRUE.equals(valueOperations.getOperations().hasKey(TOKEN_PREFIX + userId));
    }

    public String findByUserId(Long userId) {
        ValueOperations<String, String> valueOperations = getValueOperations();
        return valueOperations.get(TOKEN_PREFIX + userId);
    }
}

