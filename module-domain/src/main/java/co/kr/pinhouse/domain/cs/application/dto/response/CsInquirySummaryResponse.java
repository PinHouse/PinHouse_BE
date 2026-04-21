package co.kr.pinhouse.domain.cs.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import co.kr.pinhouse.domain.cs.domain.entity.CsInquiry;
import co.kr.pinhouse.domain.cs.domain.entity.CsInquiryCategory;
import co.kr.pinhouse.domain.cs.domain.entity.CsInquiryStatus;
import lombok.Builder;

@Builder
public record CsInquirySummaryResponse(
	Long inquiryId,
	String title,
	CsInquiryCategory category,
	CsInquiryStatus status,
	UUID assignedAdminId,
	LocalDateTime createdAt,
	LocalDateTime lastMessageAt
) {

	public static CsInquirySummaryResponse from(CsInquiry inquiry) {
		return CsInquirySummaryResponse.builder()
			.inquiryId(inquiry.getId())
			.title(inquiry.getTitle())
			.category(inquiry.getCategory())
			.status(inquiry.getStatus())
			.assignedAdminId(inquiry.getAssignedAdminId())
			.createdAt(inquiry.getCreatedAt())
			.lastMessageAt(inquiry.getLastMessageAt())
			.build();
	}
}
