package com.pinHouse.server.platform.user.application.dto;

import com.pinHouse.server.security.oauth2.domain.TempUserInfo;
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
        String username)
{

    /// 정적 팩토리 메서드
    public static TempUserResponse from(TempUserInfo tempUserInfo) {
        return TempUserResponse.builder()
                .social(tempUserInfo.getSocial())
                .email(tempUserInfo.getEmail())
                .username(tempUserInfo.getUsername())
                .build();
    }

}
