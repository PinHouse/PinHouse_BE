package co.kr.pinhouse.domain.cs.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import co.kr.pinhouse.domain.BaseTimeEntity;
import co.kr.pinhouse.domain.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cs_inquiries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CsInquiry extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private CsInquiryCategory category;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private CsInquiryStatus status;

	@Column(nullable = false)
	private String title;

	@Column(name = "assigned_admin_id", columnDefinition = "BINARY(16)")
	private UUID assignedAdminId;

	@Column(name = "first_responded_at")
	private LocalDateTime firstRespondedAt;

	@Column(name = "resolved_at")
	private LocalDateTime resolvedAt;

	@Column(name = "last_message_at", nullable = false)
	private LocalDateTime lastMessageAt;

	@Builder
	protected CsInquiry(
		User user,
		CsInquiryCategory category,
		CsInquiryStatus status,
		String title,
		UUID assignedAdminId,
		LocalDateTime firstRespondedAt,
		LocalDateTime resolvedAt,
		LocalDateTime lastMessageAt
	) {
		this.user = user;
		this.category = category;
		this.status = status;
		this.title = title;
		this.assignedAdminId = assignedAdminId;
		this.firstRespondedAt = firstRespondedAt;
		this.resolvedAt = resolvedAt;
		this.lastMessageAt = lastMessageAt;
	}

	public static CsInquiry create(User user, CsInquiryCategory category, String title) {
		return CsInquiry.builder()
			.user(user)
			.category(category)
			.status(CsInquiryStatus.RECEIVED)
			.title(title)
			.lastMessageAt(LocalDateTime.now())
			.build();
	}

	public void assignAdmin(UUID adminId) {
		this.assignedAdminId = adminId;
	}

	public void changeStatus(CsInquiryStatus status) {
		this.status = status;
		if (status == CsInquiryStatus.RESOLVED || status == CsInquiryStatus.CLOSED) {
			this.resolvedAt = LocalDateTime.now();
		} else {
			this.resolvedAt = null;
		}
	}

	public void markUserMessage() {
		this.lastMessageAt = LocalDateTime.now();
		if (this.status == CsInquiryStatus.WAITING_USER) {
			this.status = CsInquiryStatus.IN_PROGRESS;
		}
	}

	public void markAdminResponse() {
		LocalDateTime now = LocalDateTime.now();
		if (this.firstRespondedAt == null) {
			this.firstRespondedAt = now;
		}
		this.lastMessageAt = now;
		if (this.status == CsInquiryStatus.RECEIVED || this.status == CsInquiryStatus.WAITING_USER) {
			this.status = CsInquiryStatus.IN_PROGRESS;
		}
	}
}
