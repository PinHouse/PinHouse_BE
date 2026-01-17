package com.pinHouse.server.platform.image.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Presigned URL 생성 응답 DTO
 */
@Schema(name = "[응답][이미지] Presigned URL 생성 응답")
public record PresignedUrlResponse(

        @Schema(description = "클라이언트가 PUT 요청할 Presigned URL", example = "https://pinhouse-images-local.s3.ap-northeast-2.amazonaws.com/profile/...")
        String presignedUrl,

        @Schema(description = "User.profileImage에 저장할 최종 이미지 URL", example = "https://pinhouse-images-local.s3.ap-northeast-2.amazonaws.com/profile/a1b2c3d4.../f8a9b0c1....jpg")
        String imageUrl,

        @Schema(description = "Presigned URL 만료 시간 (초)", example = "600")
        int expiresIn
) {

    /**
     * 정적 팩토리 메서드
     */
    public static PresignedUrlResponse of(String presignedUrl, String imageUrl, int expiresIn) {
        return new PresignedUrlResponse(presignedUrl, imageUrl, expiresIn);
    }
}
