package co.kr.pinhouse.domain.cs.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCsInquiryMessageRequest(
	@NotBlank String content
) {
}
