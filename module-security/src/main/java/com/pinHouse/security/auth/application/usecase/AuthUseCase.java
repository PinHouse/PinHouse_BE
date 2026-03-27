package com.pinHouse.security.auth.application.usecase;

import com.pinHouse.security.jwt.application.dto.JwtTokenResponse;

import java.util.Optional;
import java.util.UUID;

public interface AuthUseCase {

    /// 재발급 하기
    JwtTokenResponse reissue(Optional<String> refreshToken);

    /// 로그아웃 진행하기
    void logout(UUID userId, Optional<String> refreshToken);

    /// 토큰 여부 판단하기
    boolean checkToken(Optional<String> accessToken);

}
