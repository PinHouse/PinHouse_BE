package com.pinHouse.server.security.jwt.domain.repository;

import com.pinHouse.server.security.jwt.domain.entity.JwtRefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface JwtRefreshTokenRepository extends CrudRepository<JwtRefreshToken, String> {

    Optional<JwtRefreshToken> findByRefreshToken(String refreshToken);

    Optional<JwtRefreshToken> findByRefreshTokenAndUserId(String refreshToken, UUID userId);


    void deleteByRefreshToken(String refreshToken);

    Optional<JwtRefreshToken> findByUserId(UUID userId);


}
