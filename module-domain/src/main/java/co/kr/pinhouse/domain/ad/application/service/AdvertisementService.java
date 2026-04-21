package co.kr.pinhouse.domain.ad.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.pinhouse.common.exception.code.AdErrorCode;
import co.kr.pinhouse.common.response.CustomException;
import co.kr.pinhouse.common.response.pageable.SliceRequest;
import co.kr.pinhouse.common.response.pageable.SliceResponse;
import co.kr.pinhouse.domain.ad.application.dto.request.CreateAdvertisementRequest;
import co.kr.pinhouse.domain.ad.application.dto.request.RecordAdvertisementEventRequest;
import co.kr.pinhouse.domain.ad.application.dto.request.UpdateAdvertisementRequest;
import co.kr.pinhouse.domain.ad.application.dto.response.AdminAdvertisementResponse;
import co.kr.pinhouse.domain.ad.application.dto.response.AdminAdvertisementSummaryResponse;
import co.kr.pinhouse.domain.ad.application.dto.response.AdvertisementRuntimeResponse;
import co.kr.pinhouse.domain.ad.application.usecase.AdvertisementUseCase;
import co.kr.pinhouse.domain.ad.domain.entity.Advertisement;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementEvent;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementEventType;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementPlacement;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementStatus;
import co.kr.pinhouse.domain.ad.domain.repository.AdvertisementEventRepository;
import co.kr.pinhouse.domain.ad.domain.repository.AdvertisementRepository;
import co.kr.pinhouse.domain.admin.application.usecase.AdminSessionUseCase;
import co.kr.pinhouse.domain.admin.audit.application.usecase.AdminAuditLogUseCase;
import co.kr.pinhouse.domain.admin.audit.domain.entity.AdminAuditActionType;
import co.kr.pinhouse.domain.admin.audit.domain.entity.AdminAuditTargetType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdvertisementService implements AdvertisementUseCase {

	private final AdvertisementRepository advertisementRepository;
	private final AdvertisementEventRepository advertisementEventRepository;
	private final AdminSessionUseCase adminSessionService;
	private final AdminAuditLogUseCase adminAuditLogService;

	// =================
	//  관리자 로직
	// =================

	/// 관리자 광고 목록 조회
	@Transactional(readOnly = true)
	@Override
	public SliceResponse<AdminAdvertisementSummaryResponse> getAdminAdvertisements(SliceRequest sliceRequest) {
		var pageable = PageRequest.of(sliceRequest.page() - 1, sliceRequest.offSet(),
			Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));
		var page = advertisementRepository.findAllByOrderByCreatedAtDesc(pageable);

		return SliceResponse.from(page.map(AdminAdvertisementSummaryResponse::from), page.getTotalElements());
	}

	/// 관리자 광고 상세 조회
	@Transactional(readOnly = true)
	@Override
	public AdminAdvertisementResponse getAdminAdvertisement(Long advertisementId) {
		Advertisement advertisement = loadAdvertisement(advertisementId);
		return toAdminResponse(advertisement);
	}

	/// 관리자 광고 생성
	@Transactional
	@Override
	public AdminAdvertisementResponse createAdvertisement(
		CreateAdvertisementRequest request,
		UUID adminId,
		HttpServletRequest httpServletRequest
	) {
		adminSessionService.loadAdmin(adminId);
		validateSchedule(request.startAt(), request.endAt());

		Advertisement advertisement = advertisementRepository.save(Advertisement.create(
			request.title(),
			request.placement(),
			request.imageUrl(),
			request.linkType(),
			request.linkValue(),
			request.startAt(),
			request.endAt(),
			request.priority() != null ? request.priority() : 0
		));

		AdminAdvertisementResponse after = toAdminResponse(advertisement);
		adminAuditLogService.log(
			adminId,
			AdminAuditActionType.CREATE,
			AdminAuditTargetType.ADVERTISEMENT,
			String.valueOf(advertisement.getId()),
			"광고 생성",
			null,
			after,
			httpServletRequest
		);

		return after;
	}

	/// 관리자 광고 정보 수정
	@Transactional
	@Override
	public AdminAdvertisementResponse updateAdvertisement(
		Long advertisementId,
		UpdateAdvertisementRequest request,
		UUID adminId,
		HttpServletRequest httpServletRequest
	) {
		adminSessionService.loadAdmin(adminId);

		Advertisement advertisement = loadAdvertisement(advertisementId);
		AdminAdvertisementResponse before = toAdminResponse(advertisement);
		LocalDateTime nextStartAt = request.startAt() != null ? request.startAt() : advertisement.getStartAt();
		LocalDateTime nextEndAt = request.endAt() != null ? request.endAt() : advertisement.getEndAt();
		validateSchedule(nextStartAt, nextEndAt);

		advertisement.update(
			request.title(),
			request.placement(),
			request.imageUrl(),
			request.linkType(),
			request.linkValue(),
			request.startAt(),
			request.endAt(),
			request.priority()
		);

		AdminAdvertisementResponse after = toAdminResponse(advertisement);
		adminAuditLogService.log(
			adminId,
			AdminAuditActionType.UPDATE,
			AdminAuditTargetType.ADVERTISEMENT,
			String.valueOf(advertisementId),
			"광고 수정",
			before,
			after,
			httpServletRequest
		);

		return after;
	}

	/// 관리자 광고 상태 변경
	@Transactional
	@Override
	public AdminAdvertisementResponse updateStatus(
		Long advertisementId,
		AdvertisementStatus status,
		UUID adminId,
		HttpServletRequest httpServletRequest
	) {
		adminSessionService.loadAdmin(adminId);

		Advertisement advertisement = loadAdvertisement(advertisementId);
		AdminAdvertisementResponse before = toAdminResponse(advertisement);
		advertisement.changeStatus(status);
		AdminAdvertisementResponse after = toAdminResponse(advertisement);

		adminAuditLogService.log(
			adminId,
			AdminAuditActionType.STATUS_CHANGE,
			AdminAuditTargetType.ADVERTISEMENT,
			String.valueOf(advertisementId),
			"광고 상태 변경",
			before,
			after,
			httpServletRequest
		);

		return after;
	}

	// =================
	//  런타임 로직
	// =================

	/// 노출 위치별 활성 광고 조회
	@Transactional(readOnly = true)
	@Override
	public List<AdvertisementRuntimeResponse> getPlacementAdvertisements(AdvertisementPlacement placement) {
		LocalDateTime now = LocalDateTime.now();
		return advertisementRepository.findByPlacementAndStatusOrderByPriorityDescIdDesc(placement, AdvertisementStatus.ACTIVE)
			.stream()
			.filter(advertisement -> advertisement.isExposedAt(now))
			.map(AdvertisementRuntimeResponse::from)
			.toList();
	}

	/// 광고 이벤트 저장
	@Transactional
	@Override
	public void recordEvent(
		RecordAdvertisementEventRequest request,
		UUID userId,
		HttpServletRequest httpServletRequest
	) {
		Advertisement advertisement = loadAdvertisement(request.advertisementId());
		advertisementEventRepository.save(AdvertisementEvent.of(
			advertisement,
			request.eventType(),
			userId,
			extractClientIp(httpServletRequest)
		));
	}

	// =================
	//  내부 로직
	// =================

	/// 광고 단건 조회
	private Advertisement loadAdvertisement(Long advertisementId) {
		return advertisementRepository.findById(advertisementId)
			.orElseThrow(() -> new CustomException(AdErrorCode.NOT_FOUND_ADVERTISEMENT));
	}

	/// 광고 노출 기간 검증
	private void validateSchedule(LocalDateTime startAt, LocalDateTime endAt) {
		if (startAt != null && endAt != null && endAt.isBefore(startAt)) {
			throw new CustomException(AdErrorCode.BAD_REQUEST_AD_SCHEDULE);
		}
	}

	/// 관리자 응답 DTO 변환
	private AdminAdvertisementResponse toAdminResponse(Advertisement advertisement) {
		long impressionCount = advertisementEventRepository.countByAdvertisement_IdAndEventType(
			advertisement.getId(), AdvertisementEventType.IMPRESSION);
		long clickCount = advertisementEventRepository.countByAdvertisement_IdAndEventType(
			advertisement.getId(), AdvertisementEventType.CLICK);

		return AdminAdvertisementResponse.of(advertisement, impressionCount, clickCount);
	}

	/// 요청 IP 추출
	private String extractClientIp(HttpServletRequest request) {
		if (request == null) {
			return null;
		}

		String forwardedFor = request.getHeader("X-Forwarded-For");
		if (forwardedFor != null && !forwardedFor.isBlank() && !"unknown".equalsIgnoreCase(forwardedFor)) {
			return forwardedFor.split(",")[0].trim();
		}

		String realIp = request.getHeader("X-Real-IP");
		if (realIp != null && !realIp.isBlank() && !"unknown".equalsIgnoreCase(realIp)) {
			return realIp;
		}

		return request.getRemoteAddr();
	}
}
