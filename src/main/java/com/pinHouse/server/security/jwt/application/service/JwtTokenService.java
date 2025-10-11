package com.pinHouse.server.security.jwt.application.service;

import com.pinHouse.server.security.jwt.application.usecase.JwtTokenUseCase;
import com.pinHouse.server.security.jwt.application.util.JwtTokenExtractor;
import com.pinHouse.server.security.jwt.application.util.JwtTokenProvider;
import com.pinHouse.server.security.jwt.application.util.RedisUtil;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 토큰 서비스
 */
@Service
@Transactional
@RequiredArgsConstructor
public class JwtTokenService implements JwtTokenUseCase {

    private final JwtTokenProvider provider;
    private final JwtTokenExtractor extractor;

    /// 레디스
    private final RedisUtil redisUtil;

    /// 쿠키
    private final CookieUtil cookieUtil;

    // 액세스 토큰 생성하기
    @Override
    public void createAccessToken(HttpServletResponse response, Authentication authentication) {
        // 토큰을 발급한다.
        String accessToken = provider.generateAccessToken(authentication);

        // 토큰을 쿠키에 저장한다.
        cookieUtil.setAccessCookie(accessToken, response);

    }

    // 리프레쉬 토큰 생성하기
    @Override
    public void createRefreshToken(HttpServletResponse response, Authentication authentication) {

        // 토큰을 발급한다.
        String refreshToken = provider.generateRefreshToken(authentication);

        // 인증 객체에서 정보 가져오기
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        // 해당 토큰을 레디스에 저장한다.
        UUID userId = principalDetails.getId();
        redisUtil.saveRefreshToken(userId, refreshToken);

        // 토큰을 쿠키에 저장한다.
        cookieUtil.setRefreshCookie(refreshToken, response);
    }



}
