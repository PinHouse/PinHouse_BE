package co.kr.pinhouse.security.oauth2.filter;

import java.io.IOException;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import co.kr.pinhouse.common.util.RedirectUrlResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2AuthorizationRequestOriginFilter extends OncePerRequestFilter {

	// OAuth2 로그인 시작 요청에서만 프론트 Origin을 저장한다
	private static final RequestMatcher OAUTH2_AUTHORIZATION_REQUEST_MATCHER =
		new AntPathRequestMatcher("/oauth2/authorization/**");

	private final RedirectUrlResolver redirectUrlResolver;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return !OAUTH2_AUTHORIZATION_REQUEST_MATCHER.matches(request);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		// 인증 공급자로 리다이렉트되기 전에 현재 프론트 Origin과 callback path를 세션에 저장
		redirectUrlResolver.saveRedirectContext(request);
		filterChain.doFilter(request, response);
	}
}
