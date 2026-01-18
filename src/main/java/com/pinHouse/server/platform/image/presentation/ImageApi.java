package com.pinHouse.server.platform.image.presentation;

import com.pinHouse.server.core.aop.CheckLogin;
import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.image.application.dto.PresignedUrlRequest;
import com.pinHouse.server.platform.image.application.dto.PresignedUrlResponse;
import com.pinHouse.server.platform.image.application.usecase.ImageUseCase;
import com.pinHouse.server.platform.image.presentation.swagger.ImageApiSpec;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        var response = imageUseCase.generatePresignedUrl(request, principalDetails.getId());
        return ApiResponse.ok(response);
    }
}
