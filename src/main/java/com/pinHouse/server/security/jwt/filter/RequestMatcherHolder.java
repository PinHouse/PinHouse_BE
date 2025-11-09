package com.pinHouse.server.security.jwt.filter;

import com.pinHouse.server.platform.user.domain.entity.Role;
import io.micrometer.common.lang.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.parameters.P;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpMethod.*;

@Component
public class RequestMatcherHolder {

    private static final List<RequestInfo> REQUEST_INFO_LIST = List.of(

            // 공통
            new RequestInfo(OPTIONS, "/**", null),
            new RequestInfo(GET, "/", null),
            new RequestInfo(GET, "/login", null),

            // user
            new RequestInfo(GET, "/v1/user", null),     /// 임시 정보
            new RequestInfo(PATCH, "/v1/user", Role.USER),      /// 추가
            new RequestInfo(POST, "/v1/user", null),    /// 회원가입

            // auth-dev
            new RequestInfo(POST, "/v1/auth/dev", null),    /// 개발용 토큰

            // auth
            new RequestInfo(DELETE, "/v1/auth", Role.USER),     /// 로그아웃
            new RequestInfo(PUT, "/v1/auth", null),     /// 재발급
            new RequestInfo(GET, "/v1/auth", null),     /// 토큰 여부 체크

            // users
            new RequestInfo(DELETE, "/v1/users", Role.USER),     /// 회원탈퇴
            new RequestInfo(GET, "/v1/users/mypage", Role.USER),    /// 내 정보
            new RequestInfo(GET, "/v1/users/{userId}", Role.USER),      /// 다른 정보
            new RequestInfo(GET, "/v1/users", null),      /// 레디스
            new RequestInfo(PATCH, "/v1/users", Role.USER),         /// 수정
            new RequestInfo(POST, "/v1/users", null),       /// 회원가입

            // oauth2
            new RequestInfo(POST, "/api/v1/oauth2/**", null),

            // notice
            new RequestInfo(GET, "/v1/notices/likes", Role.USER),
            new RequestInfo(GET, "/v1/notices/**", null),

            // complex
            new RequestInfo(GET, "/v1/complexes/likes", Role.USER),
            new RequestInfo(GET, "/v1/complexes/**", null),

            // like
            new RequestInfo(POST, "/v1/likes/**", Role.USER),
            new RequestInfo(DELETE, "/v1/likes/**", Role.USER),

            // infra
            new RequestInfo(GET, "/v1/complexes/infra/**", null),

            // search
            new RequestInfo(GET, "/v1/search/fast", Role.USER),

            // batch
            new RequestInfo(POST, "/v1/facility/batch", null),

            // 진단 관련
            new RequestInfo(POST, "/api/v1/diagnosis/**", Role.USER),

            // static resources
            new RequestInfo(GET, "/docs/**", null),
            new RequestInfo(GET, "/*.ico", null),
            new RequestInfo(GET, "/resources/**", null),
            new RequestInfo(GET, "/style.css", null),
            new RequestInfo(GET, "/index.html", null),
            new RequestInfo(GET, "/error", null),

            // Swagger UI 및 API 문서 관련 요청
            new RequestInfo(GET, "/v3/api-docs/**", null),
            new RequestInfo(GET, "/swagger-ui/**", null),
            new RequestInfo(GET, "/swagger-resources/**", null),
            new RequestInfo(GET, "/webjars/**", null),
            new RequestInfo(GET, "/swagger-ui.html", null),

            // 정적 아이콘 요청
            new RequestInfo(GET, "/favicon.ico", null),
            new RequestInfo(GET, "/apple-touch-icon.png", null)

    );




    private final ConcurrentHashMap<String, RequestMatcher> reqMatcherCacheMap = new ConcurrentHashMap<>();

    /**
     * 최소 권한이 주어진 요청에 대한 RequestMatcher 반환
     * @param minRole 최소 권한 (Nullable)
     * @return 생성된 RequestMatcher
     */
    public RequestMatcher getRequestMatchersByMinRole(@Nullable Role minRole) {
        var key = getKeyByRole(minRole);
        return reqMatcherCacheMap.computeIfAbsent(key, k ->
                new OrRequestMatcher(REQUEST_INFO_LIST.stream()
                        .filter(reqInfo -> isAccessible(reqInfo.minRole(), minRole))
                        .map(reqInfo -> new AntPathRequestMatcher(reqInfo.pattern(),
                                reqInfo.method().name()))
                        .toArray(AntPathRequestMatcher[]::new)));
    }

    private boolean isAccessible(@Nullable Role requiredRole, @Nullable Role currentRole) {
        if (requiredRole == null) return true; // 누구나 접근 가능
        if (currentRole == null) return false; // 권한 없음
        return currentRole.ordinal() >= requiredRole.ordinal(); // ADMIN이면 MEMBER 포함
    }

    private String getKeyByRole(@Nullable Role minRole) {
        return minRole == null ? "VISITOR" : minRole.name();
    }

    private record RequestInfo(HttpMethod method, String pattern, Role minRole) {}

}
