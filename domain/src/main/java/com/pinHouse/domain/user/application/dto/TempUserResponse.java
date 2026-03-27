package com.pinHouse.domain.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.Map;

@Schema(name = "[응답][사용자] 임시 사용자 정보 응답", description = "임시 사용자 정보 응답을 위한 DTO입니다.")
@Builder
public record TempUserResponse(
        @Schema(description = "소셜 로그인 제공자", example = "KAKAO")
        String social,

        @Schema(description = "사용자 이메일", example = "user@example.com")
        String email,

        @Schema(description = "사용자 이름", example = "홍길동")
        String username)
{

    /// from Map (TempUserInfo는 security 모듈에 있으므로 Map으로 처리)
    public static TempUserResponse from(Map<String, Object> infoMap) {
        return TempUserResponse.builder()
                .social((String) infoMap.get("social"))
                .email((String) infoMap.get("email"))
                .username((String) infoMap.get("username"))
                .build();
    }

}
