package com.pinHouse.server.platform.user.application.dto.response;

import com.pinHouse.server.security.oauth2.domain.TempUserInfo;
import lombok.Builder;

@Builder
public record TempUserResponse(
        String social,
        String email,
        String username
) {

    /// 정적 팩토리 메서드
    public static TempUserResponse from(TempUserInfo tempUserInfo) {
        return TempUserResponse.builder()
                .social(tempUserInfo.getSocial())
                .email(tempUserInfo.getEmail())
                .username(tempUserInfo.getUsername())
                .build();
    }

}
