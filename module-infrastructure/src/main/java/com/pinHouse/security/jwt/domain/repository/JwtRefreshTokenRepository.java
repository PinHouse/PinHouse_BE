package com.pinHouse.security.jwt.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.pinHouse.security.jwt.domain.entity.JwtRefreshToken;

public interface JwtRefreshTokenRepository extends CrudRepository<JwtRefreshToken, String> {

	/// 유저 아이디와 토큰 기반으로 추출
	Optional<JwtRefreshToken> findByUserIdAndRefreshToken(UUID userId, String refreshToken);
}
