package com.pinHouse.server.platform.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "[요청][사용자] 사용자 정보 수정 Request", description = "사용자의 닉네임과 프로필 이미지 URL을 수정하기 위한 요청 DTO입니다.")
public record UpdateUserRequest(

        @Schema(description = "수정할 닉네임", example = "newNickname")
        String nickname,

        @Schema(description = "수정할 프로필 이미지 URL", example = "https://cdn.example.com/profile/newImage.png")
        String imageUrl
) {

}
