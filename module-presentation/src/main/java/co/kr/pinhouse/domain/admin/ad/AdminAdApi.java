package co.kr.pinhouse.domain.admin.ad;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.pinhouse.common.auth.CurrentUserId;
import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.common.response.pageable.SliceRequest;
import co.kr.pinhouse.common.response.pageable.SliceResponse;
import co.kr.pinhouse.domain.ad.application.dto.request.CreateAdvertisementRequest;
import co.kr.pinhouse.domain.ad.application.dto.request.UpdateAdvertisementRequest;
import co.kr.pinhouse.domain.ad.application.dto.request.UpdateAdvertisementStatusRequest;
import co.kr.pinhouse.domain.ad.application.dto.response.AdminAdvertisementResponse;
import co.kr.pinhouse.domain.ad.application.dto.response.AdminAdvertisementSummaryResponse;
import co.kr.pinhouse.domain.ad.application.usecase.AdvertisementUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/ads")
@RequiredArgsConstructor
@Tag(name = "관리자 광고 API", description = "관리자 광고 관리 API")
public class AdminAdApi {

	private final AdvertisementUseCase advertisementService;

	/// 관리자 광고 목록 조회
	@GetMapping
	@Operation(summary = "광고 목록 조회", description = "관리자 광고 목록을 조회합니다.")
	public ApiResponse<SliceResponse<AdminAdvertisementSummaryResponse>> getAdvertisements(SliceRequest sliceRequest) {
		return ApiResponse.ok(advertisementService.getAdminAdvertisements(sliceRequest));
	}

	/// 관리자 광고 생성
	@PostMapping
	@Operation(summary = "광고 생성", description = "새 광고를 생성합니다.")
	public ApiResponse<AdminAdvertisementResponse> createAdvertisement(
		@RequestBody @Valid CreateAdvertisementRequest request,
		@CurrentUserId(required = true) UUID userId,
		HttpServletRequest httpServletRequest
	) {
		return ApiResponse.ok(advertisementService.createAdvertisement(request, userId, httpServletRequest));
	}

	/// 관리자 광고 상세 조회
	@GetMapping("/{advertisementId}")
	@Operation(summary = "광고 상세 조회", description = "광고 상세와 성과를 조회합니다.")
	public ApiResponse<AdminAdvertisementResponse> getAdvertisement(@PathVariable Long advertisementId) {
		return ApiResponse.ok(advertisementService.getAdminAdvertisement(advertisementId));
	}

	/// 관리자 광고 정보 수정
	@PatchMapping("/{advertisementId}")
	@Operation(summary = "광고 수정", description = "광고 기본 정보를 수정합니다.")
	public ApiResponse<AdminAdvertisementResponse> updateAdvertisement(
		@PathVariable Long advertisementId,
		@RequestBody UpdateAdvertisementRequest request,
		@CurrentUserId(required = true) UUID userId,
		HttpServletRequest httpServletRequest
	) {
		return ApiResponse.ok(
			advertisementService.updateAdvertisement(advertisementId, request, userId, httpServletRequest));
	}

	/// 관리자 광고 상태 변경
	@PatchMapping("/{advertisementId}/status")
	@Operation(summary = "광고 상태 변경", description = "광고 상태를 변경합니다.")
	public ApiResponse<AdminAdvertisementResponse> updateStatus(
		@PathVariable Long advertisementId,
		@RequestBody @Valid UpdateAdvertisementStatusRequest request,
		@CurrentUserId(required = true) UUID userId,
		HttpServletRequest httpServletRequest
	) {
		return ApiResponse.ok(
			advertisementService.updateStatus(advertisementId, request.status(), userId, httpServletRequest));
	}
}
