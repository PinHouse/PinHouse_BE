package co.kr.pinhouse.domain.auth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.security.auth.application.dto.request.ExchangeCodeRequest;
import co.kr.pinhouse.security.auth.application.dto.response.AuthExchangeResponse;
import co.kr.pinhouse.security.jwt.application.dto.response.JwtTokenResponse;
import co.kr.pinhouse.security.principal.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "인증 API", description = "로그아웃/액세스 토큰 재발급을 수행하는 API입니다")
public interface AuthApiSpec {

	/// Exchange code 교환
	@Operation(
		summary = "Exchange Code 교환 API",
		description = "OAuth2 또는 회원가입 이후 받은 Exchange Code를 처리하여 토큰 발급 또는 회원가입 필요 상태를 반환합니다."
	)
	ApiResponse<AuthExchangeResponse> exchangeCode(
		HttpServletRequest httpServletRequest,
		@RequestBody @Valid ExchangeCodeRequest request
	);

	/// 로그아웃
	@Operation(
		summary = "로그아웃 API",
		description = "이미 인증된 유저가 로그아웃하는 API"
	)
	ApiResponse<Void> logout(
		HttpServletRequest httpServletRequest,
		@AuthenticationPrincipal AuthenticatedUser customUserDetails);

	/// 토큰 재발급
	@Operation(
		summary = "액세스토큰 재발급 API",
		description = "액세스 토큰을 재발급받는 API"
	)
	ApiResponse<JwtTokenResponse> reissue(
		HttpServletRequest httpServletRequest
	);

	/// 액세 토큰 여부 체크
	@Operation(
		summary = "액세스토큰 여부 체크 API",
		description = "액세스 토큰이 존재하는지 체크하는 API"
	)
	ApiResponse<Boolean> checkAccessToken(HttpServletRequest httpServletRequest);
}
