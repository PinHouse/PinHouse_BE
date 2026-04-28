package co.kr.pinhouse.domain.admin.notice.application.dto.response;

import java.time.LocalDate;

import co.kr.pinhouse.domain.admin.notice.domain.entity.NoticeAdminOverride;
import co.kr.pinhouse.domain.housing.notice.domain.entity.NoticeDocument;
import co.kr.pinhouse.domain.housing.notice.domain.entity.Urls;
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
	String actualLink,
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
			.actualLink(resolveActualLink(notice))
			.hidden(override != null && override.isHidden())
			.hasOverride(override != null)
			.build();
	}

	private static String resolve(String overrideValue, String originalValue) {
		return overrideValue != null ? overrideValue : originalValue;
	}

	private static String resolveActualLink(NoticeDocument notice) {
		Urls urls = notice.getUrls();
		if (urls == null) {
			return null;
		}

		if (hasText(urls.getApply())) {
			return urls.getApply();
		}

		if (hasText(urls.getMyhomePc())) {
			return urls.getMyhomePc();
		}

		return hasText(urls.getMyhomeMo()) ? urls.getMyhomeMo() : null;
	}

	private static boolean hasText(String value) {
		return value != null && !value.isBlank();
	}
}
