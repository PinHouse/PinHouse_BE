package com.pinHouse.server.platform.image.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.image.application.dto.PresignedUrlRequest;
import com.pinHouse.server.platform.image.application.dto.PresignedUrlResponse;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 이미지 업로드 API Swagger 명세
 */
@Tag(name = "Image API", description = "이미지 업로드 API")
public interface ImageApiSpec {

    @Operation(
            summary = "Presigned URL 생성",
            description = "프로필 이미지 업로드를 위한 S3 Presigned URL을 생성합니다. " +
                    "생성된 presignedUrl로 클라이언트가 직접 S3에 PUT 요청을 보내 이미지를 업로드합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Presigned URL 생성 성공",
                    content = @Content(schema = @Schema(implementation = PresignedUrlResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (파일 타입/크기 오류)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "S3 서버 오류",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ApiResponse<PresignedUrlResponse> generatePresignedUrl(
            @Valid PresignedUrlRequest request,
            PrincipalDetails principalDetails
    );
}
