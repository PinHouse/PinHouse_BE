package com.pinHouse.server.security.auth.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.security.auth.application.usecase.AuthUseCase;
import com.pinHouse.server.security.auth.presentation.swagger.AuthApiSpec;
import com.pinHouse.server.security.jwt.application.dto.JwtTokenResponse;
import com.pinHouse.server.security.jwt.application.util.HttpUtil;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthApi implements AuthApiSpec {

    private final AuthUseCase service;

    /// HTTP 서비스
    private final HttpUtil httpUtil;

    // =================
    //  퍼블릭 로직
    // =================

    /**
     * 로그아웃
     */
    @DeleteMapping
    public ApiResponse<Void> logout(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        /// 리프레쉬 토큰 까보기
        Optional<String> refreshToken = httpUtil.getRefreshToken(httpServletRequest);

        /// 서비스 로직 실행
        service.logout(principalDetails.getId(), refreshToken);

        /// 쿠키 삭제하기
        httpUtil.removeAccessTokenCookie(httpServletResponse);
        httpUtil.removeRefreshTokenCookie(httpServletResponse);

        /// 리턴
        return ApiResponse.deleted();
    }

    /**
     * 토큰 재발급
     */
    @PutMapping
    public ApiResponse<Void> reissue(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) {

        /// 리프레쉬 토큰 까보기
        Optional<String> refreshToken = httpUtil.getRefreshToken(httpServletRequest);

        /// 서비스 로직 실행
        JwtTokenResponse response = service.reissue(refreshToken);

        /// 액세스 쿠키로 전송하기
        httpUtil.addAccessTokenCookie(httpServletResponse, response.accessToken());

        /// 리턴
        return ApiResponse.updated();
    }

}
