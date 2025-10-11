package com.pinHouse.server.security.jwt.application.usecase;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface JwtTokenUseCase {

    // 액세스 토큰 생성하기
    void createAccessToken(HttpServletResponse response, Authentication authentication);

    // 리프레쉬 토큰 생성하기
    void createRefreshToken(HttpServletResponse response, Authentication authentication);

}
