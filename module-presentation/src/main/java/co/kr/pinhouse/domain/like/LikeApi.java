package co.kr.pinhouse.domain.like;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.pinhouse.common.auth.CurrentUserId;
import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.domain.like.application.dto.LikeRequest;
import co.kr.pinhouse.domain.like.application.usecase.LikeCommandUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/likes")
@RequiredArgsConstructor
public class LikeApi implements LikeApiSpec {

	private final LikeCommandUseCase service;

	/// 좋아요 생성
	@PostMapping
	public ApiResponse<Void> like(
		@RequestBody @Valid LikeRequest request,
		@CurrentUserId(required = true) UUID userId) {

		/// 서비스 호출
		service.saveLike(userId, request);

		/// 리턴
		return ApiResponse.created();

	}

	/// 좋아요 취소
	@DeleteMapping
	public ApiResponse<Void> disLike(
		@RequestBody @Valid LikeRequest request,
		@CurrentUserId(required = true) UUID userId) {

		/// 서비스 호출
		service.deleteLike(userId, request);

		/// 리턴
		return ApiResponse.deleted();
	}

}
