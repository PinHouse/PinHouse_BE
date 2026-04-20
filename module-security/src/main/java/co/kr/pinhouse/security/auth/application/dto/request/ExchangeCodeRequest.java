package co.kr.pinhouse.security.auth.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ExchangeCodeRequest(
	@NotBlank(message = "Exchange Code는 비어 있을 수 없습니다.")
	String code
) {

	/// 정적 팩토리 메서드
	public static ExchangeCodeRequest of(String code) {
		return ExchangeCodeRequest.builder()
			.code(code)
			.build();
	}
}
