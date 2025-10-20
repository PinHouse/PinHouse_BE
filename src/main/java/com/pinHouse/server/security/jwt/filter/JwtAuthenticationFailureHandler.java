package com.pinHouse.server.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.core.response.response.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT 인증 실패 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFailureHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        /// 401 에러코드 발생
        ErrorCode errorCode;

        errorCode = ErrorCode.fromMessage(authException.getMessage());
        if (errorCode.equals(ErrorCode.BASE_ERROR)) {
            errorCode = ErrorCode.ACCESS_TOKEN_NOT_FOUND;
        }

        CustomException exception = new CustomException(errorCode, null);
        ApiResponse<Object> apiResponse = ApiResponse.fail(exception);

        /// 응답 설정
        response.setStatus(apiResponse.httpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // JSON 응답
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
