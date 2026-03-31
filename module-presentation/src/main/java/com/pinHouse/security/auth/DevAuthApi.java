package com.pinHouse.security.auth;

import com.pinHouse.common.response.ApiResponse;
import com.pinHouse.security.auth.application.service.DevAuthService;
import com.pinHouse.security.jwt.application.dto.JwtTokenResponse;
import com.pinHouse.common.util.HttpUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("!prod")
@RestController
@RequestMapping("/v1/auth/dev")
@RequiredArgsConstructor
public class DevAuthApi implements DevAuthApiSpec {

    private final DevAuthService service;

    /// HTTP 서비스
    private final HttpUtil httpUtil;

    /// 개발용 토큰 발급
    @PostMapping()
    public ApiResponse<Void> devLogin(HttpServletResponse httpServletResponse) {

        /// 서비스
        JwtTokenResponse jwtTokenResponse = service.devCreate();

        /// 토큰 발급하기
        httpUtil.addDevAccessTokenCookie(httpServletResponse, jwtTokenResponse.accessToken());
        httpUtil.addRefreshTokenCookie(httpServletResponse, jwtTokenResponse.refreshToken());

        /// 리턴
        return ApiResponse.created();
    }
}

