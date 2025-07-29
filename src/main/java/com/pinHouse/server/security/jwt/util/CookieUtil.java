package com.pinHouse.server.security.jwt.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.pinHouse.server.security.jwt.util.TokenNameUtil.ACCESS_TOKEN_COOKIE_NAME;
import static com.pinHouse.server.security.jwt.util.TokenNameUtil.REFRESH_TOKEN_COOKIE_NAME;


@Slf4j
@Component
public class CookieUtil {

    @Value("${kikihi.jwt.access.expiration}")
    private Long accessTokenExpiration;

    @Value("${kikihi.jwt.refresh.expiration}")
    private Long refreshTokenExpiration;

    @Value("${kikihi.auth.jwt.secureOption}")
    private boolean secureOption;

    @Value("${kikihi.auth.jwt.sameSiteOption}")
    private String sameSiteOption;

    @Value("${kikihi.auth.jwt.cookiePathOption}")
    private String cookiePathOption;

    // 쿠키 저장
    public void setAccessCookie(String accessToken, HttpServletResponse response) {
        setCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken, accessTokenExpiration);
    }

    public void setRefreshCookie(String refreshToken, HttpServletResponse response) {
        setCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, refreshTokenExpiration);
    }

    // 쿠키 조회
    public Optional<String> getAccessTokenFromCookie(HttpServletRequest request) {
        return getTokenFromCookie(request, ACCESS_TOKEN_COOKIE_NAME);
    }

    public Optional<String> getRefreshTokenFromCookie(HttpServletRequest request) {
        return getTokenFromCookie(request, REFRESH_TOKEN_COOKIE_NAME);
    }

    // 쿠키 삭제
    public void deleteAccessTokenCookie(HttpServletResponse response) {
        deleteCookie(response, ACCESS_TOKEN_COOKIE_NAME);
    }

    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        deleteCookie(response, REFRESH_TOKEN_COOKIE_NAME);
    }



    // 공통 쿠키 저장 메서드
    private void setCookie(HttpServletResponse response, String cookieName, String tokenValue, long maxAge) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, tokenValue)
                .maxAge(maxAge)
                .path(cookiePathOption)
                .httpOnly(true)
                .secure(secureOption)  // Dev/Prod 환경에 따라 설정됨
                .sameSite(sameSiteOption)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }


    // 공통 쿠키 추출 메서드
    private Optional<String> getTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return Optional.ofNullable(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }



    // 공통 쿠키 삭제 메서드
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
