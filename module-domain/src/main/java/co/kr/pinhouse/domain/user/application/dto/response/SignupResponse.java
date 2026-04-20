package co.kr.pinhouse.domain.user.application.dto.response;

import lombok.Builder;

@Builder
public record SignupResponse(
	String accessToken,
	String refreshToken
) {

	/// 정적 팩토리 메서드
	public static SignupResponse of(String accessToken, String refreshToken) {
		return SignupResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}
