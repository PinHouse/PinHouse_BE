package com.pinHouse.server.platform.image.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Presigned URL 생성 요청 DTO
 */
@Schema(name = "[요청][이미지] Presigned URL 생성 요청")
public record PresignedUrlRequest(

        @NotBlank(message = "파일명은 필수 입력값입니다")
        @Schema(description = "업로드할 파일명", example = "profile.jpg", required = true)
        String fileName,

        @NotBlank(message = "Content-Type은 필수 입력값입니다")
        @Pattern(regexp = "^image/(jpeg|jpg|png|gif)$", message = "지원하지 않는 이미지 형식입니다")
        @Schema(description = "파일 Content-Type", example = "image/jpeg", required = true)
        String contentType
) {
}
