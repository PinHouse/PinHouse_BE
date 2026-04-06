package co.kr.pinhouse.domain.housing.complex;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.kr.pinhouse.common.aop.CheckLogin;
import co.kr.pinhouse.common.auth.CurrentUserId;
import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.domain.housing.complex.application.dto.response.ComplexDetailResponse;
import co.kr.pinhouse.domain.housing.complex.application.dto.response.TransitRoutesResponse;
import co.kr.pinhouse.domain.housing.complex.application.dto.response.UnitTypeResponse;
import co.kr.pinhouse.domain.housing.complex.application.usecase.ComplexUseCase;
import co.kr.pinhouse.domain.like.application.dto.UnityTypeLikeResponse;

import lombok.RequiredArgsConstructor;

/**
 * 예산 시뮬레이터 관련 API 입니다
 */
@RestController
@RequestMapping("/v1/complexes")
@RequiredArgsConstructor
public class ComplexApi implements ComplexApiSpec {

	private final ComplexUseCase service;

	/// 나의 좋아요 방 목록
	@CheckLogin
	@GetMapping("/likes")
	public ApiResponse<List<UnityTypeLikeResponse>> getLikeComplexes(
			@CurrentUserId(required = true) UUID userId
	) {

		/// 서비스 호출
		var response = service.getComplexesLikes(userId);

		/// 리턴
		return ApiResponse.ok(response);
	}

	/// 상세 조회
	@GetMapping("/{complexId}")
	public ApiResponse<ComplexDetailResponse> getComplex(
			@PathVariable String complexId,
			@RequestParam String pinPointId) throws UnsupportedEncodingException {

		/// 서비스 호출
		var response = service.getComplex(complexId, pinPointId);

		/// 리턴
		return ApiResponse.ok(response);
	}

	/// 방 타입 목록 조회
	@GetMapping("/unit/{complexId}")
	public ApiResponse<List<UnitTypeResponse>> getComplexUnitTypes(
			@PathVariable String complexId,
			@CurrentUserId UUID userId
	) {

		/// 서비스 호출 (로그인하지 않은 경우 userId는 null)
		var response = service.getComplexUnitTypes(complexId, userId);

		/// 리턴
		return ApiResponse.ok(response);
	}

	/// 대중교통 시뮬레이터 (새 스키마 - 3개 경로 한 번에)
	@GetMapping("/transit/{complexId}")
	public ApiResponse<TransitRoutesResponse> distance(
			@PathVariable String complexId,
			@RequestParam String pinPointId) throws UnsupportedEncodingException {

		/// 서비스 호출 (새 스키마)
		var response = service.getDistanceV2(complexId, pinPointId);

		/// 리턴
		return ApiResponse.ok(response);
	}

}
