package co.kr.pinhouse.security.auth.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
	name = "[응답][인증] Exchange Code 교환 응답",
	description = "Exchange Code 해석 결과를 반환합니다.",
	oneOf = {
		TokenIssuedAuthExchangeResponse.class,
		SignupRequiredAuthExchangeResponse.class
	}
)
public interface AuthExchangeResponse {

	AuthExchangeResultType result();

	default String accessToken() {
		return null;
	}

	default String refreshToken() {
		return null;
	}

	default String pinpointId() {
		return null;
	}

	default String tempKey() {
		return null;
	}

	static AuthExchangeResponse tokenIssued(String accessToken, String refreshToken, String pinpointId) {
		return new TokenIssuedAuthExchangeResponse(
			AuthExchangeResultType.TOKEN_ISSUED,
			accessToken,
			refreshToken,
			pinpointId
		);
	}

	static AuthExchangeResponse signupRequired(String tempKey) {
		return new SignupRequiredAuthExchangeResponse(
			AuthExchangeResultType.SIGNUP_REQUIRED,
			tempKey
		);
	}
}
