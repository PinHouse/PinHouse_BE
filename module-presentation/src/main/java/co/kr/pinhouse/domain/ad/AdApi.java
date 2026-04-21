package co.kr.pinhouse.domain.ad;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.pinhouse.common.auth.CurrentUserId;
import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.domain.ad.application.dto.request.RecordAdvertisementEventRequest;
import co.kr.pinhouse.domain.ad.application.dto.response.AdvertisementRuntimeResponse;
import co.kr.pinhouse.domain.ad.application.usecase.AdvertisementUseCase;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementPlacement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/ads")
@RequiredArgsConstructor
@Tag(name = "광고 Runtime API", description = "광고 노출/이벤트 API")
public class AdApi {

	private final AdvertisementUseCase advertisementService;

	/// 노출 위치별 광고 목록 조회
	@GetMapping("/placements/{placement}")
	@Operation(summary = "노출 광고 조회", description = "지정된 placement의 활성 광고를 조회합니다.")
	public ApiResponse<List<AdvertisementRuntimeResponse>> getPlacementAdvertisements(
		@PathVariable AdvertisementPlacement placement
	) {
		return ApiResponse.ok(advertisementService.getPlacementAdvertisements(placement));
	}

	/// 광고 이벤트 기록
	@PostMapping("/events")
	@Operation(summary = "광고 이벤트 기록", description = "광고 노출/클릭 이벤트를 기록합니다.")
	public ApiResponse<Void> recordEvent(
		@RequestBody @Valid RecordAdvertisementEventRequest request,
		@CurrentUserId UUID userId,
		HttpServletRequest httpServletRequest
	) {
		advertisementService.recordEvent(request, userId, httpServletRequest);
		return ApiResponse.created();
	}
}
