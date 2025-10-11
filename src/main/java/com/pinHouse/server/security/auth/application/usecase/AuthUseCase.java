package com.pinHouse.server.security.auth.application.usecase;

import com.pinHouse.server.security.jwt.application.dto.JwtTokenResponse;

import java.util.Optional;
import java.util.UUID;

public interface AuthUseCase {

    /// 재발급 하기
    JwtTokenResponse reissue(Optional<String> refreshToken);

    /// 로그아웃 진행하기
    void logout(UUID userId, Optional<String> refreshToken);

}
