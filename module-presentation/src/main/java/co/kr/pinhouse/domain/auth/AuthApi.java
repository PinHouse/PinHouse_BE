package co.kr.pinhouse.domain.auth;

import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.common.util.HttpUtil;
import co.kr.pinhouse.security.auth.application.dto.request.ExchangeCodeRequest;
import co.kr.pinhouse.security.auth.application.dto.response.AuthExchangeResponse;
import co.kr.pinhouse.security.auth.application.service.ExchangeCodeRateLimitService;
import co.kr.pinhouse.security.auth.application.usecase.AuthUseCase;
import co.kr.pinhouse.security.jwt.application.dto.response.JwtTokenResponse;
import co.kr.pinhouse.security.principal.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthApi implements AuthApiSpec {

	private final AuthUseCase service;
	private final ExchangeCodeRateLimitService exchangeCodeRateLimitService;

	/// HTTP 서비스
	private final HttpUtil httpUtil;

	// =================
	//  퍼블릭 로직
	// =================

	/**
	 * Exchange code를 JWT 토큰으로 교환
	 */
	@PostMapping("/exchange")
	public ApiResponse<AuthExchangeResponse> exchangeCode(
		HttpServletRequest httpServletRequest,
		@RequestBody @Valid ExchangeCodeRequest request
	) {
		String clientIp = httpUtil.getClientIp(httpServletRequest);
		exchangeCodeRateLimitService.validateRequestAllowed(clientIp);

		log.info("공개 토큰 교환 요청 - clientIp: {}, code: {}...",
			clientIp, request.code().substring(0, Math.min(8, request.code().length())));

		AuthExchangeResponse response = service.exchangeCode(request.code());
		return ApiResponse.ok(response);
	}

	/**
	 * 로그아웃
	 */
	@DeleteMapping
	public ApiResponse<Void> logout(
		HttpServletRequest httpServletRequest,
		@AuthenticationPrincipal AuthenticatedUser principalDetails) {

		/// 리프레쉬 토큰 까보기
		Optional<String> refreshToken = httpUtil.getRefreshToken(httpServletRequest);

		/// 서비스 로직 실행
		service.logout(principalDetails.getId(), refreshToken);

		/// 쿠키 삭제는 Next가 처리

		/// 리턴
		return ApiResponse.deleted();
	}

	/**
	 * 토큰 재발급
	 */
	@PutMapping
	public ApiResponse<JwtTokenResponse> reissue(
		HttpServletRequest httpServletRequest
	) {

		/// 리프레쉬 토큰 까보기
		Optional<String> refreshToken = httpUtil.getRefreshToken(httpServletRequest);

		/// 서비스 로직 실행
		JwtTokenResponse response = service.reissue(refreshToken);

		/// 토큰을 응답 바디로 반환
		/// Next가 쿠키로 설정
		return ApiResponse.ok(response);
	}

	/**
	 * 토큰 여부 판단
	 */
	@GetMapping()
	public ApiResponse<Boolean> checkAccessToken(HttpServletRequest httpServletRequest) {

		/// 토큰 있는지 체크
		Optional<String> accessToken = httpUtil.getAccessToken(httpServletRequest);

		/// 서비스
		var response = service.checkToken(accessToken);

		/// 리턴
		return ApiResponse.ok(response);
	}

}
