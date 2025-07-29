package com.pinHouse.server.security.jwt.util;

import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.security.jwt.domain.JwtToken;
import com.pinHouse.server.security.jwt.domain.JwtTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.NoSuchElementException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    @Value("${kikihi.jwt.refresh.expiration}")
    private Long refreshTokenExpiration;

    private final JwtTokenRedisRepository repository;


    /// 리프레쉬 토큰 저장
    public void saveRefreshToken(UUID userId, String refreshToken) {

        // TTL 설정
        LocalDateTime plusSeconds = LocalDateTime.now().plusSeconds(refreshTokenExpiration);
        Long expiredAt = plusSeconds.atZone(ZoneId.systemDefault()).toEpochSecond();

        // 해당 토큰을 레디스에 저장한다.
        JwtToken jwtToken = JwtToken.of(userId, refreshToken, expiredAt);
        repository.save(jwtToken);

    }


    /// 리프레쉬 토큰 조회
    public String getRefreshTokenByUserId(UUID userId) {

        /// 유저 Id로 리프레쉬 토큰 조회
        JwtToken jwtToken = repository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage()));

        return jwtToken.getRefreshToken();
    }

    public String getRefreshTokenById(String refreshToken) {

        /// 리프레쉬 토큰으로 유저 조회
        JwtToken jwtToken = repository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage()));

        return jwtToken.getRefreshToken();
    }

    /// 존재 여부 체크
    public boolean checkRefreshTokenAndUserId(String refreshToken, UUID userId) {
        return repository.findByRefreshToken(refreshToken)
                .map(token -> token.getUserId().equals(userId))
                .orElse(false);
    }

    /// 리프레쉬 토큰 삭제하기
    public void deleteByRefreshToken(String refreshToken) {

        repository.deleteByRefreshToken(refreshToken);
    }

}
