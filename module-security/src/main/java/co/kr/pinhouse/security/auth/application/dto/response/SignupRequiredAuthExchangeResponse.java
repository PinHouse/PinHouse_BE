package co.kr.pinhouse.security.auth.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "[응답][인증] 회원가입 필요 응답", description = "회원가입이 필요한 상태를 반환합니다.")
public record SignupRequiredAuthExchangeResponse(
	@Schema(description = "Exchange Code 처리 결과", example = "SIGNUP_REQUIRED")
	AuthExchangeResultType result,

	@Schema(description = "회원가입이 필요한 경우 사용할 임시 키", example = "OAUTH2_TEMP_USER:1234...")
	String tempKey
) implements AuthExchangeResponse {
}
