package com.pinHouse.security.auth.application.usecase;

import com.pinHouse.domain.user.domain.entity.User;
import com.pinHouse.security.jwt.application.dto.JwtTokenResponse;

import java.util.Optional;
import java.util.UUID;

public interface AuthUseCase {

    /// 액세스/리프레쉬 토큰 발급
    JwtTokenResponse issueTokens(User user);

    /// 재발급 하기
    JwtTokenResponse reissue(Optional<String> refreshToken);

    /// 로그아웃 진행하기
    void logout(UUID userId, Optional<String> refreshToken);

    /// 토큰 여부 판단하기
    boolean checkToken(Optional<String> accessToken);

}
