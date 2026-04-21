package co.kr.pinhouse.domain.admin.user.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import co.kr.pinhouse.domain.user.domain.entity.User;
import lombok.Builder;

@Builder
public record AdminUserSummaryResponse(
	UUID userId,
	String maskedName,
	String nickname,
	String maskedEmail,
	String maskedPhoneNumber,
	String provider,
	String role,
	LocalDateTime createdAt
) {

	public static AdminUserSummaryResponse of(
		User user,
		String maskedName,
		String maskedEmail,
		String maskedPhoneNumber
	) {
		return AdminUserSummaryResponse.builder()
			.userId(user.getId())
			.maskedName(maskedName)
			.nickname(user.getNickname())
			.maskedEmail(maskedEmail)
			.maskedPhoneNumber(maskedPhoneNumber)
			.provider(user.getProvider().name())
			.role(user.getRole().name())
			.createdAt(user.getCreatedAt())
			.build();
	}
}
