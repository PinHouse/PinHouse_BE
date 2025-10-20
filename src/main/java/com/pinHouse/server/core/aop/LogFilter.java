package com.pinHouse.server.core.aop;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * HTTP 요청에 대한 필터를 작성합니다.
 * 디스패처 서블릿 앞에서 필터를 작성합니다.
 */
@Slf4j
@Component
public class LogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /// 요청 로그 남기기
        String ipAddress = getClientIp(request);

        String httpMethod = request.getMethod();
        String uri = URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8);
        String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "익명";

        /// 로그 출력하기
        log.info("[HTTP 요청]: {}, [{}], {}, {}", ipAddress, httpMethod, uri, username);

        /// 로그 남기고 넘기기
        filterChain.doFilter(request, response);

    }

    /// 요청자의 실제 IP를 조회하기 위한 함수
    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        /// X-Forwarded-For이 있다면
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // 여러 개라면 첫 번째 값이 클라이언트 IP
            return ip.split(",")[0].trim();
        }

        /// X-Forwarded-For이 없다면
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
