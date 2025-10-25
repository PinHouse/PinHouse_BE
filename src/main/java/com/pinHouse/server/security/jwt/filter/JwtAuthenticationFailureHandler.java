package com.pinHouse.server.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinHouse.server.core.exception.code.CommonErrorCode;
import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.core.logging.HttpLogUtil;
import com.pinHouse.server.security.jwt.application.exception.JwtAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import static com.pinHouse.server.core.util.KeyUtil.HTTP_ERROR_401;

/**
 * JWT 인증 실패 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFailureHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final HttpLogUtil httpUtil;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        /// 인증 문제 발생시, 401 Error 발생
        CustomException exception = new CustomException(CommonErrorCode.UNAUTHORIZED);

        /// JWT 예외인 경우
        if (authException instanceof JwtAuthenticationException jwtEx) {
            exception = new CustomException(jwtEx.getErrorCode());
        }

        /// 예외 처리
        ApiResponse<Object> apiResponse = ApiResponse.fail(exception);

        /// 응답 설정
        response.setStatus(apiResponse.httpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        /// 로그 찍기
        httpUtil.logHttpRequest(request, HTTP_ERROR_401);

        /// JSON 응답
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
