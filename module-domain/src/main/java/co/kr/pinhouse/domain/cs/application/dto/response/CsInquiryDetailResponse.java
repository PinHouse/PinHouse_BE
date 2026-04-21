package co.kr.pinhouse.domain.cs.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import co.kr.pinhouse.domain.cs.domain.entity.CsInquiry;
import co.kr.pinhouse.domain.cs.domain.entity.CsInquiryCategory;
import co.kr.pinhouse.domain.cs.domain.entity.CsInquiryStatus;
import lombok.Builder;

@Builder
public record CsInquiryDetailResponse(
	Long inquiryId,
	String title,
	CsInquiryCategory category,
	CsInquiryStatus status,
	UUID assignedAdminId,
	LocalDateTime createdAt,
	LocalDateTime firstRespondedAt,
	LocalDateTime resolvedAt,
	CsInquiryRequesterResponse requester,
	List<CsInquiryMessageResponse> messages
) {

	public static CsInquiryDetailResponse of(
		CsInquiry inquiry,
		CsInquiryRequesterResponse requester,
		List<CsInquiryMessageResponse> messages
	) {
		return CsInquiryDetailResponse.builder()
			.inquiryId(inquiry.getId())
			.title(inquiry.getTitle())
			.category(inquiry.getCategory())
			.status(inquiry.getStatus())
			.assignedAdminId(inquiry.getAssignedAdminId())
			.createdAt(inquiry.getCreatedAt())
			.firstRespondedAt(inquiry.getFirstRespondedAt())
			.resolvedAt(inquiry.getResolvedAt())
			.requester(requester)
			.messages(messages)
			.build();
	}
}
