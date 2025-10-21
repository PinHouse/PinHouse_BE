package com.pinHouse.server.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.core.logging.HttpLogUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import static com.pinHouse.server.core.response.response.ErrorCode.ACCESS_TOKEN_NOT_FOUND;
import static com.pinHouse.server.core.util.KeyUtil.HTTP_ERROR_403;

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

        /// 401 에러코드 발생
        Optional<ErrorCode> errorCodeOptional = ErrorCode.fromMessage(authException.getMessage());

        /// 없으면 기본 401
        ErrorCode errorCode = errorCodeOptional
                .orElse(ACCESS_TOKEN_NOT_FOUND);

        /// 예외 처리
        CustomException exception = new CustomException(errorCode, null);
        ApiResponse<Object> apiResponse = ApiResponse.fail(exception);

        /// 응답 설정
        response.setStatus(apiResponse.httpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        /// 로그 찍기
        httpUtil.logHttpRequest(request, HTTP_ERROR_403);

        /// JSON 응답
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
