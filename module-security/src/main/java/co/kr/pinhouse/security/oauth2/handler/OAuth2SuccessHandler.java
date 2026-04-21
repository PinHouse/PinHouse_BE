package co.kr.pinhouse.security.oauth2.handler;

import static co.kr.pinhouse.common.util.LogSanitizer.sanitize;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import co.kr.pinhouse.common.util.RedirectUrlResolver;
import co.kr.pinhouse.domain.user.domain.entity.User;
import co.kr.pinhouse.security.auth.application.usecase.AuthUseCase;
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
	private final RedirectUrlResolver redirectUrlResolver;

	/*
		기존에 존재하는 유저의 경우, Exchange code를 발급하고 BFF로 리다이렉트합니다.
	 */

	@Override
	public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
		Authentication authentication) throws IOException, ServletException {

		try {
			/// 인증객체에서 User 가져오기
			PrincipalDetails principal = (PrincipalDetails)authentication.getPrincipal();
			User user = principal.getUser();

			/// Exchange code 생성
			String exchangeCode = authUseCase.createExchangeCode(user);

			/// 시큐리티 홀더에 해당 멤버 저장
			SecurityContextHolder.getContext().setAuthentication(authentication);

			/// BFF 콜백 URL 생성
			String redirectUrl = buildBffCallbackUrl(httpServletRequest, exchangeCode);

			log.info("OAuth2 인증 성공 - userId: {}, BFF 리다이렉트: {}",
				sanitize(user.getId()), sanitize(redirectUrl));

			/// BFF로 리다이렉트 (exchange code 포함)
			getRedirectStrategy().sendRedirect(httpServletRequest, httpServletResponse, redirectUrl);
		} catch (Exception e) {
			log.error("OAuth2 인증 처리 중 에러 발생", e);
			throw e;
		}
	}

	private String buildBffCallbackUrl(HttpServletRequest request, String exchangeCode) {
		String baseUrl = resolveBffCallbackUrl(request);
		return baseUrl + "?code=" + exchangeCode;
	}

	private String resolveBffCallbackUrl(HttpServletRequest request) {
		return redirectUrlResolver.resolveRedirectUrl(request);
	}
}
