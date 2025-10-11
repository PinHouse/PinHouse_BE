package com.pinHouse.server.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.core.response.response.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT 인가 실패 핸들러
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        ErrorCode errorCode = ErrorCode.fromMessage(accessDeniedException.getMessage());

        /// 403 에러 코드 발생
        CustomException exception = new CustomException(errorCode, null);
        ApiResponse<Object> apiResponse = ApiResponse.fail(exception);

        /// response 제작
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // JSON 응답
        objectMapper.writeValue(response.getWriter(), apiResponse);

    }
}
