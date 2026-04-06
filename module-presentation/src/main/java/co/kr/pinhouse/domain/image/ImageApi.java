package co.kr.pinhouse.domain.image;

import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.pinhouse.common.aop.CheckLogin;
import co.kr.pinhouse.common.auth.CurrentUserId;
import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.domain.image.application.dto.PresignedUrlRequest;
import co.kr.pinhouse.domain.image.application.dto.PresignedUrlResponse;
import co.kr.pinhouse.domain.image.application.usecase.ImageUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
