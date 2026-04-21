package co.kr.pinhouse.domain.cs.domain.entity;

import java.util.UUID;

import co.kr.pinhouse.domain.BaseTimeEntity;
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
@Table(name = "cs_inquiry_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CsInquiryMessage extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inquiry_id", nullable = false)
	private CsInquiry inquiry;

	@Enumerated(EnumType.STRING)
	@Column(name = "sender_type", nullable = false, length = 30)
	private CsMessageSenderType senderType;

	@Column(name = "sender_id", nullable = false, columnDefinition = "BINARY(16)")
	private UUID senderId;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Builder
	protected CsInquiryMessage(
		CsInquiry inquiry,
		CsMessageSenderType senderType,
		UUID senderId,
		String content
	) {
		this.inquiry = inquiry;
		this.senderType = senderType;
		this.senderId = senderId;
		this.content = content;
	}

	public static CsInquiryMessage of(
		CsInquiry inquiry,
		CsMessageSenderType senderType,
		UUID senderId,
		String content
	) {
		return CsInquiryMessage.builder()
			.inquiry(inquiry)
			.senderType(senderType)
			.senderId(senderId)
			.content(content)
			.build();
	}
}
