package co.kr.pinhouse.domain.user.application.dto;

import java.util.Map;

import co.kr.pinhouse.common.dto.TempUserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "[응답][사용자] 임시 사용자 정보 응답", description = "임시 사용자 정보 응답을 위한 DTO입니다.")
@Builder
public record TempUserResponse(
	@Schema(description = "소셜 로그인 제공자", example = "KAKAO")
	String social,

	@Schema(description = "사용자 이메일", example = "user@example.com")
	String email,

	@Schema(description = "사용자 이름", example = "홍길동")
	String username) {

	/// from TempUserInfo
	public static TempUserResponse from(TempUserInfo userInfo) {
		return TempUserResponse.builder()
			.social(userInfo.getSocial())
			.email(userInfo.getEmail())
			.username(userInfo.getUsername())
			.build();
	}

	/// from Map (하위 호환성)
	public static TempUserResponse from(Map<String, Object> infoMap) {
		return TempUserResponse.builder()
			.social((String)infoMap.get("social"))
			.email((String)infoMap.get("email"))
			.username((String)infoMap.get("username"))
			.build();
	}

}
