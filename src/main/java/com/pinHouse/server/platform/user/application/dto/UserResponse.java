package com.pinHouse.server.platform.user.application.dto;

import com.pinHouse.server.platform.user.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponse(
        @Schema(description = "유저 식별자", example = "1")
        UUID userId,

        @Schema(description = "프로필 이미지 URL", example = "https://cdn.example.com/profile/1.png")
        String imageUrl,

        @Schema(description = "닉네임", example = "이도연")
        String name
) {

    /// 정적 팩토리 메서드
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .imageUrl(user.getProfileImage())
                .name(user.getName())
                .build();
    }

}
