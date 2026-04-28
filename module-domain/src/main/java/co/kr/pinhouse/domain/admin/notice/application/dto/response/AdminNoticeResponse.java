package co.kr.pinhouse.domain.admin.notice.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import co.kr.pinhouse.domain.admin.notice.domain.entity.NoticeAdminOverride;
import co.kr.pinhouse.domain.housing.notice.domain.entity.NoticeDocument;
import co.kr.pinhouse.domain.housing.notice.domain.entity.Urls;
import lombok.Builder;

@Builder
public record AdminNoticeResponse(
	String noticeId,
	String originalTitle,
	String displayTitle,
	String originalStatus,
	String displayStatus,
	String agency,
	String originalThumbnail,
	String displayThumbnail,
	String originalContact,
	String displayContact,
	String city,
	String county,
	LocalDate announceDate,
	LocalDate applyStart,
	LocalDate applyEnd,
	String actualLink,
	boolean hidden,
	String adminMemo,
	boolean hasOverride,
	LocalDateTime overrideUpdatedAt
) {

	public static AdminNoticeResponse from(NoticeDocument notice, NoticeAdminOverride override) {
		return AdminNoticeResponse.builder()
			.noticeId(notice.getId())
			.originalTitle(notice.getTitle())
			.displayTitle(resolve(override != null ? override.getDisplayTitle() : null, notice.getTitle()))
			.originalStatus(notice.getStatus())
			.displayStatus(resolve(override != null ? override.getDisplayStatus() : null, notice.getStatus()))
			.agency(notice.getAgency())
			.originalThumbnail(notice.getThumbnail())
			.displayThumbnail(resolve(override != null ? override.getDisplayThumbnail() : null, notice.getThumbnail()))
			.originalContact(notice.getContact())
			.displayContact(resolve(override != null ? override.getDisplayContact() : null, notice.getContact()))
			.city(notice.getCity())
			.county(notice.getCounty())
			.announceDate(notice.getAnnounceDate())
			.applyStart(notice.getApplyStart())
			.applyEnd(notice.getApplyEnd())
			.actualLink(resolveActualLink(notice))
			.hidden(override != null && override.isHidden())
			.adminMemo(override != null ? override.getAdminMemo() : null)
			.hasOverride(override != null)
			.overrideUpdatedAt(override != null ? override.getUpdatedAt() : null)
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
