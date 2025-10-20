package com.pinHouse.server.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.security.jwt.application.util.HttpLogUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.pinHouse.server.core.util.KeyUtil.HTTP_ERROR_401;
/**
 * JWT 인가 실패 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final HttpLogUtil httpUtil;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        /// 기본 값 설정
        ErrorCode errorCode;


        /// 에러 코드
        errorCode = ErrorCode.fromMessage(accessDeniedException.getMessage());
        if (errorCode.equals(ErrorCode.BASE_ERROR)) {
            errorCode = ErrorCode.FORBIDDEN;
        }
        /// 403 에러 코드 발생
        CustomException exception = new CustomException(errorCode, null);
        ApiResponse<Object> apiResponse = ApiResponse.fail(exception);

        /// response 제작
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        /// 로그 찍기
        httpUtil.logHttpRequest(request, HTTP_ERROR_401);

        /// JSON 응답
        objectMapper.writeValue(response.getWriter(), apiResponse);

    }
}
