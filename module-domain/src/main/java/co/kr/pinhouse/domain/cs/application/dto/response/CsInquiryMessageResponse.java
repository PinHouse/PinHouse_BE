package co.kr.pinhouse.domain.cs.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import co.kr.pinhouse.domain.cs.domain.entity.CsInquiryMessage;
import co.kr.pinhouse.domain.cs.domain.entity.CsMessageSenderType;
import lombok.Builder;

@Builder
public record CsInquiryMessageResponse(
	Long id,
	CsMessageSenderType senderType,
	UUID senderId,
	String content,
	LocalDateTime createdAt
) {

	public static CsInquiryMessageResponse from(CsInquiryMessage message) {
		return CsInquiryMessageResponse.builder()
			.id(message.getId())
			.senderType(message.getSenderType())
			.senderId(message.getSenderId())
			.content(message.getContent())
			.createdAt(message.getCreatedAt())
			.build();
	}
}
