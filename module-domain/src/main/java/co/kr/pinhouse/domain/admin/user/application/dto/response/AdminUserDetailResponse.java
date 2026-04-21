package co.kr.pinhouse.domain.admin.user.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import co.kr.pinhouse.domain.housing.facility.domain.entity.FacilityType;
import co.kr.pinhouse.domain.user.domain.entity.User;
import lombok.Builder;

@Builder
public record AdminUserDetailResponse(
	UUID userId,
	String maskedName,
	String nickname,
	String maskedEmail,
	String maskedPhoneNumber,
	String provider,
	String role,
	String profileImage,
	LocalDateTime createdAt,
	List<FacilityType> facilityTypes,
	long likeCount,
	long pinPointCount,
	long diagnosisCount
) {

	public static AdminUserDetailResponse of(
		User user,
		String maskedName,
		String maskedEmail,
		String maskedPhoneNumber,
		long likeCount,
		long pinPointCount,
		long diagnosisCount
	) {
		return AdminUserDetailResponse.builder()
			.userId(user.getId())
			.maskedName(maskedName)
			.nickname(user.getNickname())
			.maskedEmail(maskedEmail)
			.maskedPhoneNumber(maskedPhoneNumber)
			.provider(user.getProvider().name())
			.role(user.getRole().name())
			.profileImage(user.getProfileImage())
			.createdAt(user.getCreatedAt())
			.facilityTypes(user.getFacilityTypes())
			.likeCount(likeCount)
			.pinPointCount(pinPointCount)
			.diagnosisCount(diagnosisCount)
			.build();
	}
}
