package com.pinHouse.server.security.jwt.application.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.pinHouse.server.core.util.RedisKeyUtil.ACCESS_TOKEN;
import static com.pinHouse.server.core.util.RedisKeyUtil.REFRESH_TOKEN;

@Component
public class HttpUtil {

    @Value("${auth.jwt.access.expiration}")
    private long accessExpiration;

    @Value("${auth.jwt.refresh.expiration}")
    private long refreshExpiration;

    @Value("${auth.cookie.secureOption}")
    private boolean secureOption;

    @Value("${auth.cookie.sameSiteOption}")
    private String sameSiteOption;

    @Value("${auth.cookie.PathOption}")
    private String cookiePathOption;


    /// 액세스 토큰 쿠키 가져오기
    public Optional<String> getAccessToken(HttpServletRequest request) {
        return extractToken(request, ACCESS_TOKEN);
    }

    /// 리프레쉬 토큰 쿠키 가져오기
    public Optional<String> getRefreshToken(HttpServletRequest request) {
        return extractToken(request, REFRESH_TOKEN);
    }

    /// 액세스 토큰을 쿠키에 저장하기
    public void addAccessTokenCookie(HttpServletResponse httpServletResponse, String accessToken) {

        /// 쿠키 생성 및 저장
        createCookie(httpServletResponse, ACCESS_TOKEN, accessToken, accessExpiration);

    }

    /// 리프레쉬 토큰을 쿠키에 저장하기
    public void addRefreshTokenCookie(HttpServletResponse httpServletResponse, String refreshToken) {

        /// 쿠키 생성 및 저장
        createCookie(httpServletResponse, REFRESH_TOKEN, refreshToken, refreshExpiration);
    }

    /// 액세스 토큰을 삭제하기
    public void removeAccessTokenCookie(HttpServletResponse httpServletResponse) {
        deleteCookie(httpServletResponse, ACCESS_TOKEN);
    }

    /// 리프레쉬 토큰을 삭제하기
    public void removeRefreshTokenCookie(HttpServletResponse httpServletResponse) {
        deleteCookie(httpServletResponse, REFRESH_TOKEN);
    }


    // =================
    //  내부 공통 함수
    // =================

    /// 쿠키 생성하기
    private void createCookie(HttpServletResponse response, String cookieName, String cookieValue, long maxAge) {

        ResponseCookie cookie = ResponseCookie.from(cookieName, cookieValue)
                .maxAge(maxAge)
                .path(cookiePathOption)
                .httpOnly(true)
                .secure(secureOption)  // Dev/Prod 환경에 따라 설정됨
                .sameSite(sameSiteOption)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    }

    /// 쿠키에서 토큰 가져오기
    private Optional<String> extractToken(HttpServletRequest httpServletRequest, String type) {

        /// 쿠키 가져오기
        Cookie[] cookies = httpServletRequest.getCookies();

        /// 쿠키가 존재한다면,
        if (cookies != null) {
            for (Cookie cookie : cookies) {

                /// 해당 타입의 쿠키만 추출
                if (cookie.getName().equals(type)) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    /// 공통 쿠키 삭제 메서드
    private void deleteCookie(HttpServletResponse response, String cookieName) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .maxAge(0)
                .path(cookiePathOption)
                .secure(secureOption)
                .httpOnly(true)
                .sameSite(sameSiteOption)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
