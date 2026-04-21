package co.kr.pinhouse.domain.cs.application.dto.response;

import java.util.UUID;

import lombok.Builder;

@Builder
public record CsInquiryRequesterResponse(
	UUID userId,
	String maskedName,
	String maskedEmail
) {
}
