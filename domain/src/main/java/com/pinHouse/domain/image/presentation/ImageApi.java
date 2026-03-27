package com.pinHouse.domain.image.presentation;

import java.util.UUID;

import com.pinHouse.common.aop.CheckLogin;
import com.pinHouse.common.auth.CurrentUserId;
import com.pinHouse.common.response.ApiResponse;
import com.pinHouse.domain.image.application.dto.PresignedUrlRequest;
import com.pinHouse.domain.image.application.dto.PresignedUrlResponse;
import com.pinHouse.domain.image.application.usecase.ImageUseCase;
import com.pinHouse.domain.image.presentation.swagger.ImageApiSpec;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 이미지 업로드 API 컨트롤러
 */
@RestController
@RequestMapping("/v1/images")
@RequiredArgsConstructor
public class ImageApi implements ImageApiSpec {

    private final ImageUseCase imageUseCase;

    /**
     * Presigned URL 생성 엔드포인트
     */
    @PostMapping("/presigned-url")
    @CheckLogin
    @Override
    public ApiResponse<PresignedUrlResponse> generatePresignedUrl(
            @RequestBody @Valid PresignedUrlRequest request,
            @CurrentUserId(required = true) UUID userId) {

        var response = imageUseCase.generatePresignedUrl(request, userId);
        return ApiResponse.ok(response);
    }
}
