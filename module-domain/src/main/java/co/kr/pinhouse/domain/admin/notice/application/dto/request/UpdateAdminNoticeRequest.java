package co.kr.pinhouse.domain.admin.notice.application.dto.request;

public record UpdateAdminNoticeRequest(
	String displayTitle,
	String displayStatus,
	String displayThumbnail,
	String displayContact,
	Boolean hidden,
	String adminMemo
) {
}
