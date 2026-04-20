package co.kr.pinhouse.security.auth.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "[응답][인증] Exchange Code 교환 응답", description = "Exchange Code 해석 결과를 반환합니다.")
@Builder
public record AuthExchangeResponse(
	@Schema(description = "Exchange Code 처리 결과", example = "TOKEN_ISSUED")
	AuthExchangeResultType result,

	@Schema(description = "발급된 액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
	String accessToken,

	@Schema(description = "발급된 리프레쉬 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
	String refreshToken,

	@Schema(description = "회원가입이 필요한 경우 사용할 임시 키", example = "OAUTH2_TEMP_USER:1234...")
	String tempKey
) {

	public static AuthExchangeResponse tokenIssued(String accessToken, String refreshToken) {
		return AuthExchangeResponse.builder()
			.result(AuthExchangeResultType.TOKEN_ISSUED)
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	public static AuthExchangeResponse signupRequired(String tempKey) {
		return AuthExchangeResponse.builder()
			.result(AuthExchangeResultType.SIGNUP_REQUIRED)
			.tempKey(tempKey)
			.build();
	}
}
