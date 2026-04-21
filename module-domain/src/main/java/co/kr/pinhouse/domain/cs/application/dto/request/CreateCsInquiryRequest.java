package co.kr.pinhouse.domain.cs.application.dto.request;

import co.kr.pinhouse.domain.cs.domain.entity.CsInquiryCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCsInquiryRequest(
	@NotBlank String title,
	@NotNull CsInquiryCategory category,
	@NotBlank String content
) {
}
