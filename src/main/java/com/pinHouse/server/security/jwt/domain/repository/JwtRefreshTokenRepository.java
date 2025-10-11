package com.pinHouse.server.security.jwt.domain.repository;

import com.pinHouse.server.security.jwt.domain.entity.JwtRefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface JwtRefreshTokenRepository extends CrudRepository<JwtRefreshToken, String> {

    /// 유저 아이디와 토큰 기반으로 추출
    Optional<JwtRefreshToken> findByUserIdAndRefreshToken(UUID userId, String refreshToken);
}
