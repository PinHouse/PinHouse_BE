package co.kr.pinhouse.domain.user.application.dto;

import java.util.UUID;

import co.kr.pinhouse.domain.user.domain.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "[응답][사용자] 사용자 정보 Response", description = "사용자 정보를 포함하는 응답 DTO입니다.")
public record UserResponse(
		@Schema(description = "유저 식별자", example = "UUID")
		UUID userId,

		@Schema(description = "프로필 이미지 URL", example = "https://cdn.example.com/profile/1.png")
		String imageUrl,

		@Schema(description = "닉네임", example = "단단한 집")
		String nickName
) {

	/// 정적 팩토리 메서드
	public static UserResponse from(User user) {
		return UserResponse.builder()
				.userId(user.getId())
				.imageUrl(user.getProfileImage())
				.nickName(user.getNickname())
				.build();
	}

}
