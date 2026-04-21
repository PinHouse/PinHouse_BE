package co.kr.pinhouse.domain.admin.notice.domain.entity;

import co.kr.pinhouse.domain.BaseTimeEntity;
import co.kr.pinhouse.domain.admin.notice.application.dto.request.UpdateAdminNoticeRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notice_admin_overrides")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeAdminOverride extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "notice_id", nullable = false, unique = true)
	private String noticeId;

	@Column(name = "display_title")
	private String displayTitle;

	@Column(name = "display_status")
	private String displayStatus;

	@Column(name = "display_thumbnail")
	private String displayThumbnail;

	@Column(name = "display_contact")
	private String displayContact;

	@Column(name = "is_hidden", nullable = false)
	private boolean hidden;

	@Column(name = "admin_memo", columnDefinition = "TEXT")
	private String adminMemo;

	@Builder
	protected NoticeAdminOverride(
		String noticeId,
		String displayTitle,
		String displayStatus,
		String displayThumbnail,
		String displayContact,
		boolean hidden,
		String adminMemo
	) {
		this.noticeId = noticeId;
		this.displayTitle = displayTitle;
		this.displayStatus = displayStatus;
		this.displayThumbnail = displayThumbnail;
		this.displayContact = displayContact;
		this.hidden = hidden;
		this.adminMemo = adminMemo;
	}

	public static NoticeAdminOverride create(String noticeId) {
		return NoticeAdminOverride.builder()
			.noticeId(noticeId)
			.hidden(false)
			.build();
	}

	public void apply(UpdateAdminNoticeRequest request) {
		if (request.displayTitle() != null) {
			this.displayTitle = request.displayTitle();
		}
		if (request.displayStatus() != null) {
			this.displayStatus = request.displayStatus();
		}
		if (request.displayThumbnail() != null) {
			this.displayThumbnail = request.displayThumbnail();
		}
		if (request.displayContact() != null) {
			this.displayContact = request.displayContact();
		}
		if (request.hidden() != null) {
			this.hidden = request.hidden();
		}
		if (request.adminMemo() != null) {
			this.adminMemo = request.adminMemo();
		}
	}
}
