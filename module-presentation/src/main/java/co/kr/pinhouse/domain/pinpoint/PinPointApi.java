package co.kr.pinhouse.domain.pinpoint;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.kr.pinhouse.common.aop.CheckLogin;
import co.kr.pinhouse.common.auth.CurrentUserId;
import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.domain.pinpoint.application.dto.response.PinPointListResponse;
import co.kr.pinhouse.domain.pinpoint.application.dto.request.PinPointRequest;
import co.kr.pinhouse.domain.pinpoint.application.dto.request.UpdatePinPointRequest;
import co.kr.pinhouse.domain.pinpoint.application.usecase.PinPointUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/pinpoints")
@RequiredArgsConstructor
public class PinPointApi implements PinPointApiSpec {

	private final PinPointUseCase service;

	/// 핀포인트 생성하기
	@CheckLogin
	@PostMapping()
	public ApiResponse<Void> addPinPoint(
		@CurrentUserId(required = true) UUID userId,
		@RequestBody @Valid PinPointRequest request) {

		/// 서비스
		service.savePinPoint(userId, request);

		/// 리턴
		return ApiResponse.created();
	}

	/// 핀포인트 이름 수정하기
	@CheckLogin
	@PatchMapping("{id}")
	public ApiResponse<Void> updatePinPoint(
		@PathVariable String id,
		@CurrentUserId(required = true) UUID userId,
		@RequestBody @Valid UpdatePinPointRequest request) {

		/// 서비스
		service.update(id, userId, request);

		/// 리턴
		return ApiResponse.created();
	}

	/// 나의 핀포인트 목록 조회하기
	@CheckLogin
	@GetMapping()
	public ApiResponse<PinPointListResponse> getPinPoints(
		@CurrentUserId(required = true) UUID userId
	) {

		/// 서비스
		var response = service.loadPinPoints(userId);

		/// 리턴
		return ApiResponse.ok(response);
	}

	/// 핀포인트 제거하기
	@DeleteMapping()
	@CheckLogin
	public ApiResponse<Void> deletePinPoint(
		@RequestParam String id,
		@CurrentUserId(required = true) UUID userId
	) {

		/// 서비스
		service.deletePinPoint(userId, id);

		/// 리턴
		return ApiResponse.deleted();
	}
}
