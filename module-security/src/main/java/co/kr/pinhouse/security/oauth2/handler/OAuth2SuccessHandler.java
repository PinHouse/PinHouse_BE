package co.kr.pinhouse.security.oauth2.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import co.kr.pinhouse.common.util.HttpUtil;
import co.kr.pinhouse.common.util.RedirectUrlResolver;
import co.kr.pinhouse.domain.user.domain.entity.User;
import co.kr.pinhouse.security.auth.application.usecase.AuthUseCase;
import co.kr.pinhouse.security.jwt.application.dto.response.JwtTokenResponse;
import co.kr.pinhouse.security.principal.PrincipalDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final AuthUseCase authUseCase;
	private final HttpUtil httpUtil;
	private final RedirectUrlResolver redirectUrlResolver;

	/*
		기존에 존재하는 유저의 경우, 토큰 발급을 진행합니다.
		리다이렉트 시킵니다.
	 */

	@Override
	public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
		Authentication authentication) throws IOException, ServletException {

		try {
			/// 인증객체에서 User 가져오기
			PrincipalDetails principal = (PrincipalDetails)authentication.getPrincipal();
			User user = principal.getUser();

			/// Access, Refresh 토큰 생성
			JwtTokenResponse tokenResponse = authUseCase.issueTokens(user);

			/// HTTP 쿠키 추가
			httpUtil.addAccessTokenCookie(httpServletResponse, tokenResponse.accessToken());
			httpUtil.addRefreshTokenCookie(httpServletResponse, tokenResponse.refreshToken());

			/// 시큐리티 홀더에 해당 멤버 저장
			SecurityContextHolder.getContext().setAuthentication(authentication);

			/// 동적으로 리다이렉트 URL 결정
			String redirectUrl = redirectUrlResolver.resolveRedirectUrl(httpServletRequest);

			/// 쿠키와 함께 리다이렉트 (프론트 홈 주소)
			getRedirectStrategy().sendRedirect(httpServletRequest, httpServletResponse, redirectUrl);
		} catch (Exception e) {
			log.error("OAuth2 회원가입 진행중 에러 발생", e);
		}
	}
}
