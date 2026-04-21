package co.kr.pinhouse.domain.cs.application.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record AssignCsInquiryRequest(
	@NotNull UUID adminId
) {
}
