package com.pinHouse.server.platform.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateUserRequest(

        @Schema(description = "수정할 닉네임", example = "newNickname")
        String nickname,

        @Schema(description = "수정할 프로필 이미지 URL", example = "https://cdn.example.com/profile/newImage.png")
        String imageUrl
) {

}
