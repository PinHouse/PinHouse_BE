package co.kr.pinhouse.domain.admin.notice.application.dto.response;

import java.time.LocalDate;

import co.kr.pinhouse.domain.admin.notice.domain.entity.NoticeAdminOverride;
import co.kr.pinhouse.domain.housing.notice.domain.entity.NoticeDocument;
import lombok.Builder;

@Builder
public record AdminNoticeSummaryResponse(
	String noticeId,
	String title,
	String status,
	String agency,
	LocalDate announceDate,
	LocalDate applyStart,
	LocalDate applyEnd,
	boolean hidden,
	boolean hasOverride
) {

	public static AdminNoticeSummaryResponse from(NoticeDocument notice, NoticeAdminOverride override) {
		return AdminNoticeSummaryResponse.builder()
			.noticeId(notice.getId())
			.title(resolve(override != null ? override.getDisplayTitle() : null, notice.getTitle()))
			.status(resolve(override != null ? override.getDisplayStatus() : null, notice.getStatus()))
			.agency(notice.getAgency())
			.announceDate(notice.getAnnounceDate())
			.applyStart(notice.getApplyStart())
			.applyEnd(notice.getApplyEnd())
			.hidden(override != null && override.isHidden())
			.hasOverride(override != null)
			.build();
	}

	private static String resolve(String overrideValue, String originalValue) {
		return overrideValue != null ? overrideValue : originalValue;
	}
}
