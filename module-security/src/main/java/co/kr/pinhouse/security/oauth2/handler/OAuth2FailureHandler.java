package co.kr.pinhouse.security.oauth2.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import co.kr.pinhouse.common.util.RedirectUrlResolver;
import co.kr.pinhouse.domain.user.domain.onboarding.TempUserInfo;
import co.kr.pinhouse.security.auth.application.usecase.AuthUseCase;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final AuthUseCase authUseCase;
	private final RedirectUrlResolver redirectUrlResolver;

	/**
	 * 실패 핸들러 예외 처리
	 */
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {

		/// SignupRequiredException 예외처리 받았으면 실행
		if (authException instanceof SignupRequiredException) {

			/// 임시 유저 처리
			TempUserInfo userInfo = ((SignupRequiredException)authException).getUserInfo();

			/// 회원가입 필요 상태를 나타내는 Exchange code 생성
			String exchangeCode = authUseCase.createSignupExchangeCode(userInfo);

			/// 동적으로 리다이렉트 URL 결정
			String callbackUrl = buildCallbackUrl(request, exchangeCode);

			/// 응답을 리다이렉트
			response.sendRedirect(callbackUrl);
		} else {
			response.sendRedirect("/login?error");
		}
	}

	private String buildCallbackUrl(HttpServletRequest request, String exchangeCode) {
		String baseUrl = resolveCallbackUrl(request);
		return baseUrl + "?code=" + exchangeCode;
	}

	private String resolveCallbackUrl(HttpServletRequest request) {
		String frontUrl = redirectUrlResolver.resolveRedirectUrl(request);
		return frontUrl + "/api/auth/callback";
	}
}
