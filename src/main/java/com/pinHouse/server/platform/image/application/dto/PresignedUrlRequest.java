package com.pinHouse.server.platform.image.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
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

        @Min(value = 1, message = "파일 크기는 1바이트 이상이어야 합니다")
        @Schema(description = "파일 크기 (바이트)", example = "2097152", required = true)
        long fileSize,

        @NotBlank(message = "Content-Type은 필수 입력값입니다")
        @Pattern(regexp = "^image/(jpeg|jpg|png|gif)$", message = "지원하지 않는 이미지 형식입니다")
        @Schema(description = "파일 Content-Type", example = "image/jpeg", required = true)
        String contentType
) {
}
