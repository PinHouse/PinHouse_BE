package com.pinHouse.server.security.jwt.domain.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@Builder
@RedisHash
public class JwtRefreshToken {

    @Id
    private UUID userId;

    @Indexed
    private String refreshToken;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long expireTime;

    /// 정적 팩토리 메소드
    public static JwtRefreshToken of(UUID userId, String refreshToken, Long expireTime) {
        return JwtRefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .expireTime(expireTime)
                .build();
    }

}
