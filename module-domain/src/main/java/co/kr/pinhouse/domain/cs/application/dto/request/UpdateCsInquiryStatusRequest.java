package co.kr.pinhouse.domain.cs.application.dto.request;

import co.kr.pinhouse.domain.cs.domain.entity.CsInquiryStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateCsInquiryStatusRequest(
	@NotNull CsInquiryStatus status
) {
}
