package co.kr.pinhouse.security.auth.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "[응답][인증] 토큰 발급 응답", description = "토큰 발급 완료 상태를 반환합니다.")
public record TokenIssuedAuthExchangeResponse(
	@Schema(description = "Exchange Code 처리 결과", example = "TOKEN_ISSUED")
	AuthExchangeResultType result,

	@Schema(description = "발급된 액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
	String accessToken,

	@Schema(description = "발급된 리프레쉬 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
	String refreshToken,

	@Schema(description = "로그인 완료 시 대표 PinPoint ID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
	@JsonInclude(JsonInclude.Include.ALWAYS)
	String pinpointId
) implements AuthExchangeResponse {
}
