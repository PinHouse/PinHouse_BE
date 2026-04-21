package co.kr.pinhouse.domain.ad.application.usecase;

import java.util.List;
import java.util.UUID;

import co.kr.pinhouse.common.response.pageable.SliceRequest;
import co.kr.pinhouse.common.response.pageable.SliceResponse;
import co.kr.pinhouse.domain.ad.application.dto.request.CreateAdvertisementRequest;
import co.kr.pinhouse.domain.ad.application.dto.request.RecordAdvertisementEventRequest;
import co.kr.pinhouse.domain.ad.application.dto.request.UpdateAdvertisementRequest;
import co.kr.pinhouse.domain.ad.application.dto.response.AdminAdvertisementResponse;
import co.kr.pinhouse.domain.ad.application.dto.response.AdminAdvertisementSummaryResponse;
import co.kr.pinhouse.domain.ad.application.dto.response.AdvertisementRuntimeResponse;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementPlacement;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementStatus;
import jakarta.servlet.http.HttpServletRequest;

public interface AdvertisementUseCase {

	// =================
	//  관리자 로직
	// =================

	/// 관리자 광고 목록 조회
	SliceResponse<AdminAdvertisementSummaryResponse> getAdminAdvertisements(SliceRequest sliceRequest);

	/// 관리자 광고 상세 조회
	AdminAdvertisementResponse getAdminAdvertisement(Long advertisementId);

	/// 관리자 광고 생성
	AdminAdvertisementResponse createAdvertisement(
		CreateAdvertisementRequest request,
		UUID adminId,
		HttpServletRequest httpServletRequest
	);

	/// 관리자 광고 정보 수정
	AdminAdvertisementResponse updateAdvertisement(
		Long advertisementId,
		UpdateAdvertisementRequest request,
		UUID adminId,
		HttpServletRequest httpServletRequest
	);

	/// 관리자 광고 상태 변경
	AdminAdvertisementResponse updateStatus(
		Long advertisementId,
		AdvertisementStatus status,
		UUID adminId,
		HttpServletRequest httpServletRequest
	);

	// =================
	//  런타임 로직
	// =================

	/// 노출 위치별 활성 광고 조회
	List<AdvertisementRuntimeResponse> getPlacementAdvertisements(AdvertisementPlacement placement);

	/// 광고 이벤트 기록
	void recordEvent(
		RecordAdvertisementEventRequest request,
		UUID userId,
		HttpServletRequest httpServletRequest
	);
}
