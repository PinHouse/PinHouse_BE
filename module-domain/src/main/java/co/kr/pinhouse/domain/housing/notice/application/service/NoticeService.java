package co.kr.pinhouse.domain.housing.notice.application.service;

import static co.kr.pinhouse.common.util.LogSanitizer.sanitize;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.pinhouse.common.exception.code.NoticeErrorCode;
import co.kr.pinhouse.common.response.CustomException;
import co.kr.pinhouse.common.response.pageable.SliceRequest;
import co.kr.pinhouse.common.response.pageable.SliceResponse;
import co.kr.pinhouse.common.util.TimeFormatter;
import co.kr.pinhouse.domain.Location;
import co.kr.pinhouse.domain.housing.complex.application.usecase.ComplexUseCase;
import co.kr.pinhouse.domain.housing.complex.domain.entity.ComplexDocument;
import co.kr.pinhouse.domain.housing.facility.application.dto.response.NoticeFacilityListResponse;
import co.kr.pinhouse.domain.housing.facility.application.usecase.FacilityUseCase;
import co.kr.pinhouse.domain.housing.facility.domain.entity.FacilityType;
import co.kr.pinhouse.domain.housing.notice.application.dto.UnitTypeSortType;
import co.kr.pinhouse.domain.housing.notice.application.dto.request.NoticeDetailFilterRequest;
import co.kr.pinhouse.domain.housing.notice.application.dto.request.NoticeListRequest;
import co.kr.pinhouse.domain.housing.notice.application.dto.response.ComplexFilterResponse;
import co.kr.pinhouse.domain.housing.notice.application.dto.response.NoticeDetailFilteredResponse;
import co.kr.pinhouse.domain.housing.notice.application.dto.response.NoticeListResponse;
import co.kr.pinhouse.domain.housing.notice.application.dto.response.UnitTypeCompareResponse;
import co.kr.pinhouse.domain.housing.notice.application.usecase.NoticeUseCase;
import co.kr.pinhouse.domain.housing.notice.domain.entity.NoticeDocument;
import co.kr.pinhouse.domain.housing.notice.domain.repository.NoticeDocumentRepository;
import co.kr.pinhouse.domain.like.application.usecase.LikeQueryUseCase;
import co.kr.pinhouse.domain.pinpoint.application.usecase.PinPointUseCase;
import co.kr.pinhouse.domain.pinpoint.domain.entity.PinPoint;
import co.kr.pinhouse.domain.search.domain.entity.HouseType;
import co.kr.pinhouse.domain.search.domain.entity.RentalType;
import co.kr.pinhouse.domain.search.domain.entity.SearchHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService implements NoticeUseCase {

	private final NoticeDocumentRepository repository;
	private final ComplexUseCase complexService;
	private final ComplexFilterService complexFilterService;

	/// 좋아요 목록 조회
	private final LikeQueryUseCase likeService;
	private final FacilityUseCase facilityService;
	private final PinPointUseCase pinPointService;

	/// 거리 캐싱
	private final co.kr.pinhouse.domain.housing.complex.application.service.DistanceCacheService distanceCacheService;

	// =================
	//  퍼블릭 로직
	// =================
	@Override
	public SliceResponse<NoticeListResponse> getNotices(NoticeListRequest request, SliceRequest sliceRequest,
		UUID userId) {

		/// 오늘(한국) 기준 Instant
		Instant now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant();

		/// 정렬 조건 및 pageable 설정 (동적 쿼리에 포함)
		Sort sort = (request.sortType() == NoticeListRequest.ListSortType.END)
			? Sort.by(Sort.Order.asc("applyEnd"), Sort.Order.asc("noticeId"))
			: Sort.by(Sort.Order.desc("announceDate"), Sort.Order.desc("noticeId"));
		Pageable pageable = PageRequest.of(sliceRequest.page() - 1, sliceRequest.offSet(), sort);

		/// DB 레벨 필터링을 위한 커스텀 Repository 호출
		Page<NoticeDocument> page = repository.findNoticesByFilters(request, pageable, now);

		/// 좋아요 상태 조회 (userId가 null이면 빈 목록)
		List<String> likedNoticeIds = (userId != null)
			? likeService.getLikeNoticeIds(userId)
			: List.of();

		List<NoticeListResponse> content = page.getContent().stream()
			.map(notice -> {
				boolean isLiked = likedNoticeIds.contains(notice.getId());
				return NoticeListResponse.from(notice, isLiked);
			})
			.toList();

		return SliceResponse.from(new SliceImpl<>(content, pageable, page.hasNext()), page.getTotalElements());
	}

	@Override
	public Long countNotices(NoticeListRequest request) {
		return 0L;
	}

	/// 공고 상세 조회
	@Override
	@Transactional(readOnly = true)
	public NoticeDetailFilteredResponse getNotice(String noticeId, NoticeDetailFilterRequest request) {

		/// 공고 조회
		NoticeDocument notice = loadNotice(noticeId);

		/// 단지 목록 조회
		List<ComplexDocument> complexes = complexService.loadComplexes(noticeId);

		/// 단지별 인프라 정보 조회
		Map<String, NoticeFacilityListResponse> facilityMap = complexes.stream()
			.map(ComplexDocument::getId)
			.collect(Collectors.toMap(
				id -> id,
				facilityService::getNearFacilities
			));

		/// pinPointId가 있는 경우, 모든 Complex에 대해 거리 정보를 미리 계산하고 totalTime만 저장
		Map<String, Integer> totalTimeMap = new HashMap<>();
		if (request.pinPointId() != null && !request.pinPointId().isBlank()) {
			log.info("공고 상세조회: 모든 단지에 대한 거리 계산 시작 - noticeId={}, pinPointId={}, 단지 개수={}",
				sanitize(noticeId), sanitize(request.pinPointId()), sanitize(complexes.size()));

			int successCount = 0;
			int failCount = 0;

			for (ComplexDocument complex : complexes) {
				try {
					co.kr.pinhouse.domain.housing.complex.application.dto.response.DistanceResponse distance =
						complexService.getEasyDistance(complex.getId(), request.pinPointId());
					totalTimeMap.put(complex.getId(), distance.totalTimeMinutes());
					successCount++;
					log.debug("거리 계산 성공 및 Redis 캐싱 완료 - complexId={}, totalTime={}분",
						sanitize(complex.getId()), sanitize(distance.totalTimeMinutes()));
				} catch (Exception e) {
					failCount++;
					totalTimeMap.put(complex.getId(), 0);
					log.error("거리 계산 실패 (0분으로 설정) - complexId={}, pinPointId={}, error={}",
						sanitize(complex.getId()), sanitize(request.pinPointId()), sanitize(e.getMessage()), e);
				}
			}

			log.info("거리 계산 완료 - 성공: {}, 실패: {}, 총: {}",
				sanitize(successCount), sanitize(failCount), sanitize(complexes.size()));
		}

		/// 서비스 레이어에서 필터링 수행 (totalTimeMap 전달)
		ComplexFilterService.FilterResult filterResult =
			complexFilterService.filterComplexes(complexes, facilityMap, request, totalTimeMap);

		/// DTO 정적 팩토리 메서드로 응답 생성 (이미 필터링된 데이터 전달)
		if (!totalTimeMap.isEmpty()) {
			return NoticeDetailFilteredResponse.from(
				notice,
				filterResult.filtered(),
				filterResult.nonFiltered(),
				facilityMap,
				totalTimeMap
			);
		} else {
			return NoticeDetailFilteredResponse.from(
				notice,
				filterResult.filtered(),
				filterResult.nonFiltered(),
				facilityMap
			);
		}
	}

	/// 공고의 단지 필터링 정보 조회
	@Override
	@Transactional(readOnly = true)
	public ComplexFilterResponse getComplexFilters(String noticeId) {

		/// 공고 존재 확인
		loadNotice(noticeId);

		/// 단지 목록 조회
		List<ComplexDocument> complexes = complexService.loadComplexes(noticeId);

		/// 서비스 레이어에서 필터 정보 계산
		return complexFilterService.buildFilterResponse(complexes);
	}

	/// 공고의 단지 지역 필터 정보 조회
	@Override
	@Transactional(readOnly = true)
	public ComplexFilterResponse.DistrictFilter getDistrictFilter(String noticeId) {

		/// 공고 존재 확인
		loadNotice(noticeId);

		/// 단지 목록 조회
		List<ComplexDocument> complexes = complexService.loadComplexes(noticeId);

		/// 지역 필터 계산
		return complexFilterService.calculateDistrictFilter(complexes);
	}

	/// 공고의 단지 비용 필터 정보 조회
	@Override
	@Transactional(readOnly = true)
	public ComplexFilterResponse.CostFilter getCostFilter(String noticeId) {

		/// 공고 존재 확인
		loadNotice(noticeId);

		/// 단지 목록 조회
		List<ComplexDocument> complexes = complexService.loadComplexes(noticeId);

		/// 비용 필터 계산
		return complexFilterService.calculateCostFilter(complexes);
	}

	/// 공고의 단지 방타입 필터 정보 조회
	@Override
	@Transactional(readOnly = true)
	public ComplexFilterResponse.AreaFilter getAreaFilter(String noticeId) {

		/// 공고 존재 확인
		loadNotice(noticeId);

		/// 단지 목록 조회
		List<ComplexDocument> complexes = complexService.loadComplexes(noticeId);

		/// 방타입 필터 계산
		return complexFilterService.calculateAreaFilter(complexes);
	}

	/// 공고의 필터 조건에 맞는 단지 개수 조회
	@Override
	@Transactional(readOnly = true)
	public int countFilteredComplexes(String noticeId, NoticeDetailFilterRequest request) {

		/// 공고 존재 확인
		loadNotice(noticeId);

		/// 단지 목록 조회
		List<ComplexDocument> complexes = complexService.loadComplexes(noticeId);

		/// 단지별 인프라 정보 조회
		Map<String, NoticeFacilityListResponse> facilityMap = complexes.stream()
			.map(ComplexDocument::getId)
			.collect(Collectors.toMap(
				id -> id,
				facilityService::getNearFacilities
			));

		/// pinPointId가 있는 경우, 거리 정보 계산하고 totalTime만 저장
		Map<String, Integer> totalTimeMap = new HashMap<>();
		if (request.pinPointId() != null && !request.pinPointId().isBlank()) {
			for (ComplexDocument complex : complexes) {
				try {
					co.kr.pinhouse.domain.housing.complex.application.dto.response.DistanceResponse distance =
						complexService.getEasyDistance(complex.getId(), request.pinPointId());
					totalTimeMap.put(complex.getId(), distance.totalTimeMinutes());
				} catch (Exception e) {
					totalTimeMap.put(complex.getId(), 0);
					log.error("거리 계산 실패 (0분으로 설정) - complexId={}, pinPointId={}, error={}",
						sanitize(complex.getId()), sanitize(request.pinPointId()), sanitize(e.getMessage()));
				}
			}
		}

		/// 필터 조건에 맞는 단지 개수 반환
		return complexFilterService.countMatchingComplexes(complexes, facilityMap, request, totalTimeMap);
	}

	/// 유닛타입(방) 비교
	@Override
	@Transactional(readOnly = true)
	public UnitTypeCompareResponse compareUnitTypes(
		String noticeId,
		String pinPointId,
		UnitTypeSortType sortType,
		List<FacilityType> nearbyFacilities,
		UUID userId
	) {

		/// 공고 존재 확인
		loadNotice(noticeId);

		/// 정렬 기준 설정 (null이면 기본값)
		UnitTypeSortType finalSortType = sortType != null ? sortType : UnitTypeSortType.DEPOSIT_ASC;

		/// DISTANCE_ASC 정렬은 pinPointId 필수
		if (finalSortType == UnitTypeSortType.DISTANCE_ASC && (pinPointId == null || pinPointId.isBlank())) {
			log.warn("거리순 정렬 요청이지만 pinPointId가 없어 기본 정렬(DEPOSIT_ASC)로 변경");
			finalSortType = UnitTypeSortType.DEPOSIT_ASC;
		}

		/// FACILITY_MATCH 정렬은 nearbyFacilities 필수
		if (finalSortType == UnitTypeSortType.FACILITY_MATCH && (nearbyFacilities == null
			|| nearbyFacilities.isEmpty())) {
			log.error("주변환경매칭순 정렬 요청이지만 nearbyFacilities가 없음 - noticeId={}", sanitize(noticeId));
			throw new CustomException(NoticeErrorCode.MISSING_NEARBY_FACILITIES);
		}

		/// ⭐️ DB 레벨에서 정렬된 단지 및 유닛타입 조회
		/// FACILITY_MATCH, DISTANCE_ASC의 경우 DB 정렬 없이 전체 조회 (애플리케이션 레벨 정렬 예정)
		List<ComplexDocument> complexes;
		if (finalSortType == UnitTypeSortType.FACILITY_MATCH || finalSortType == UnitTypeSortType.DISTANCE_ASC) {
			complexes = complexService.loadComplexes(noticeId);
			log.debug("{} 정렬 - 정렬 없이 {} 개 단지 조회", sanitize(finalSortType), sanitize(complexes.size()));
		} else {
			complexes = complexService.loadSortedComplexes(noticeId, finalSortType);
			log.debug("DB 정렬 완료 - 총 {} 개 단지 조회", sanitize(complexes.size()));
		}

		/// PinPoint 위치 조회 (optional)
		Location userLocation = null;
		if (pinPointId != null && !pinPointId.isBlank()) {
			try {
				PinPoint pinPoint = pinPointService.loadPinPoint(pinPointId);
				userLocation = pinPoint.getLocation();
			} catch (Exception e) {
				log.warn("Failed to load PinPoint: {}", sanitize(pinPointId), e);
			}
		}

		/// 좋아요 상태 조회 (userId가 null이면 빈 목록)
		List<String> likedUnitTypeIds = (userId != null)
			? likeService.getLikeUnitTypeIds(userId)
			: List.of();

		/// 각 단지의 시설 정보 조회
		Map<String, List<FacilityType>> facilityMap = complexes.stream()
			.collect(Collectors.toMap(
				ComplexDocument::getId,
				complex -> {
					NoticeFacilityListResponse facilityResponse = facilityService.getNearFacilities(complex.getId());
					return facilityResponse != null ? facilityResponse.infra() : List.of();
				}
			));

		/// 각 단지의 대중교통 소요 시간 계산 (pinPointId가 있는 경우에만)
		Map<String, String> totalTimeMap = new HashMap<>();
		if (pinPointId != null && !pinPointId.isBlank()) {
			log.info("방 비교: 모든 단지에 대한 대중교통 소요 시간 계산 시작 - pinPointId={}, 단지 개수={}",
				sanitize(pinPointId), sanitize(complexes.size()));

			int successCount = 0;
			int failCount = 0;

			for (ComplexDocument complex : complexes) {
				try {
					co.kr.pinhouse.domain.housing.complex.application.dto.response.DistanceResponse distance =
						complexService.getEasyDistance(complex.getId(), pinPointId);
					String formattedTime = TimeFormatter.formatTime(distance.totalTimeMinutes());
					totalTimeMap.put(complex.getId(), formattedTime);
					successCount++;
					log.debug("대중교통 시간 계산 성공 및 Redis 캐싱 완료 - complexId={}, totalTime={}",
						sanitize(complex.getId()), sanitize(formattedTime));
				} catch (Exception e) {
					failCount++;
					totalTimeMap.put(complex.getId(), null);
					log.error("대중교통 시간 계산 실패 (null로 설정) - complexId={}, pinPointId={}, error={}",
						sanitize(complex.getId()), sanitize(pinPointId), sanitize(e.getMessage()), e);
				}
			}

			log.info("대중교통 시간 계산 완료 - 성공: {}, 실패: {}, 총: {}",
				sanitize(successCount), sanitize(failCount), sanitize(complexes.size()));
		}

		/// 최종 시간 맵 (람다에서 사용하기 위해 effectively final)
		Map<String, String> finalTotalTimeMap = totalTimeMap;

		/// 모든 단지의 유닛타입을 수집하여 비교 항목 생성
		List<UnitTypeCompareResponse.UnitTypeComparisonItem> comparisonItems = complexes.stream()
			.flatMap(complex -> {
				String complexId = complex.getId();
				List<FacilityType> facilities = facilityMap.getOrDefault(complexId, List.of());
				String totalTime = finalTotalTimeMap.getOrDefault(complexId, null);

				return complex.getUnitTypes().stream()
					.map(unitType -> {
						boolean isLiked = likedUnitTypeIds.contains(unitType.getTypeId());
						return UnitTypeCompareResponse.UnitTypeComparisonItem.from(
							complex, unitType, facilities, totalTime, isLiked
						);
					});
			})
			.collect(Collectors.toList());

		/// DB에서 단지별로 다시 group 되기 때문에, 최종 응답 직전 방 목록을 한 번 더 전역 정렬한다.
		/// 이렇게 해야 단지 경계와 무관하게 "공고 내 모든 방" 기준 순서가 보장된다.
		switch (finalSortType) {
			case DEPOSIT_ASC -> {
				sortByDeposit(comparisonItems);
				log.debug("보증금순 전역 정렬 완료");
			}
			case AREA_DESC -> {
				sortByArea(comparisonItems);
				log.debug("면적순 전역 정렬 완료");
			}
			case FACILITY_MATCH -> {
				sortByFacilityMatch(comparisonItems, nearbyFacilities);
				log.debug("시설 매칭 정렬 완료 - 매칭 대상 시설: {}", sanitize(nearbyFacilities));
			}
			case DISTANCE_ASC -> {
				sortByDistance(comparisonItems);
				log.debug("거리순 정렬 완료");
			}
		}

		/// DTO 정적 팩토리 메서드로 응답 생성
		return UnitTypeCompareResponse.from(comparisonItems);
	}

	/**
	 * 보증금 기반 전역 정렬
	 *
	 * 정렬 우선순위:
	 * 1. 보증금 (낮은 순)
	 * 2. 지역 (오름차순)
	 * 3. 단지명 (오름차순)
	 * 4. 방 이름 (오름차순)
	 */
	private void sortByDeposit(List<UnitTypeCompareResponse.UnitTypeComparisonItem> items) {
		items.sort(Comparator
			// 응답 스펙 기준으로 공고 전체 방을 보증금 오름차순으로 다시 정렬
			.comparingLong(this::extractDeposit)
			.thenComparing(this::extractComplexAddress)
			.thenComparing(this::extractComplexName)
			.thenComparing(this::extractTypeCode)
		);
	}

	/**
	 * 면적 기반 전역 정렬
	 *
	 * 정렬 우선순위:
	 * 1. 면적 (넓은 순)
	 * 2. 지역 (오름차순)
	 * 3. 단지명 (오름차순)
	 * 4. 방 이름 (오름차순)
	 */
	private void sortByArea(List<UnitTypeCompareResponse.UnitTypeComparisonItem> items) {
		items.sort(Comparator
			// 단지별 묶음을 깨고 공고 전체 방을 면적 내림차순으로 다시 정렬
			.comparingDouble(this::extractArea)
			.reversed()
			.thenComparing(this::extractComplexAddress)
			.thenComparing(this::extractComplexName)
			.thenComparing(this::extractTypeCode)
		);
	}

	/**
	 * 시설 매칭 기반 정렬
	 *
	 * 정렬 우선순위:
	 * 1. 시설 매칭 개수 (많은 순)
	 * 2. 보증금 (낮은 순)
	 * 3. 지역 (오름차순)
	 * 4. 단지명 (오름차순)
	 * 5. 방 이름 (오름차순)
	 */
	private void sortByFacilityMatch(
		List<UnitTypeCompareResponse.UnitTypeComparisonItem> items,
		List<FacilityType> targetFacilities
	) {
		items.sort(Comparator
			// 1차: 시설 매칭 개수 (많은 순 = 내림차순)
			.comparing((UnitTypeCompareResponse.UnitTypeComparisonItem item) -> {
				List<FacilityType> itemFacilities = item.nearbyFacilities();
				if (itemFacilities == null || itemFacilities.isEmpty()) {
					return 0;
				}
				// targetFacilities와 itemFacilities의 교집합 개수 계산
				return (int)targetFacilities.stream()
					.filter(itemFacilities::contains)
					.count();
			}).reversed()
			// 2차: 보증금 (낮은 순 = 오름차순)
			.thenComparingLong(this::extractDeposit)
			// 3차: 지역 (오름차순)
			.thenComparing(this::extractComplexAddress)
			// 4차: 단지명 (오름차순)
			.thenComparing(this::extractComplexName)
			// 5차: 방 이름 (오름차순)
			.thenComparing(this::extractTypeCode)
		);
	}

	/**
	 * 거리 기반 정렬
	 *
	 * 정렬 우선순위:
	 * 1. 핀포인트로부터의 거리 (가까운 순)
	 * 2. 보증금 (낮은 순)
	 * 3. 지역 (오름차순)
	 * 4. 단지명 (오름차순)
	 * 5. 방 이름 (오름차순)
	 */
	private void sortByDistance(List<UnitTypeCompareResponse.UnitTypeComparisonItem> items) {
		items.sort(Comparator
			// 1차: 대중교통 소요 시간 (짧은 순 = 오름차순)
			.comparing((UnitTypeCompareResponse.UnitTypeComparisonItem item) -> {
				String totalTimeStr = item.totalTime();
				if (totalTimeStr == null || totalTimeStr.isBlank()) {
					return Integer.MAX_VALUE;
				}
				// "1시간 30분" 또는 "45분" 형식을 분 단위 int로 변환
				return parseTimeToMinutes(totalTimeStr);
			})
			// 2차: 보증금 (낮은 순)
			.thenComparingLong(this::extractDeposit)
			// 3차: 지역 (오름차순)
			.thenComparing(this::extractComplexAddress)
			// 4차: 단지명 (오름차순)
			.thenComparing(this::extractComplexName)
			// 5차: 방 이름 (오름차순)
			.thenComparing(this::extractTypeCode)
		);
	}

	private long extractDeposit(UnitTypeCompareResponse.UnitTypeComparisonItem item) {
		// 보증금 정보가 없으면 항상 뒤로 밀리도록 최대값으로 처리
		return item.cost() != null ? item.cost().totalDeposit() : Long.MAX_VALUE;
	}

	private double extractArea(UnitTypeCompareResponse.UnitTypeComparisonItem item) {
		// 면적 정보가 없으면 내림차순 정렬에서 항상 뒤로 밀리도록 최소값으로 처리
		return item.area() != null ? item.area().exclusiveAreaM2() : Double.NEGATIVE_INFINITY;
	}

	private String extractComplexAddress(UnitTypeCompareResponse.UnitTypeComparisonItem item) {
		// tie-break 시 null-safe 하게 비교하기 위한 공통 추출 로직
		return item.complex() != null && item.complex().address() != null
			? item.complex().address()
			: "";
	}

	private String extractComplexName(UnitTypeCompareResponse.UnitTypeComparisonItem item) {
		// 단지명이 없으면 빈 문자열로 비교해 정렬 안정성을 유지
		return item.complex() != null && item.complex().name() != null
			? item.complex().name()
			: "";
	}

	private String extractTypeCode(UnitTypeCompareResponse.UnitTypeComparisonItem item) {
		// 최종 tie-break 용 방 타입코드
		return item.typeCode() != null ? item.typeCode() : "";
	}

	/**
	 * 거리 문자열을 km 단위 double로 변환
	 * @param distanceStr "3.5km" 또는 "500m" 형식
	 * @return km 단위 거리
	 */
	private double parseDistanceToKm(String distanceStr) {
		try {
			if (distanceStr.endsWith("km")) {
				// "3.5km" → 3.5
				return Double.parseDouble(distanceStr.replace("km", ""));
			} else if (distanceStr.endsWith("m")) {
				// "500m" → 0.5
				double meters = Double.parseDouble(distanceStr.replace("m", ""));
				return meters / 1000.0;
			}
			return Double.MAX_VALUE;
		} catch (NumberFormatException e) {
			log.warn("거리 문자열 파싱 실패: {}", sanitize(distanceStr));
			return Double.MAX_VALUE;
		}
	}

	/**
	 * 시간 문자열을 분 단위 int로 변환
	 * @param timeStr "1시간 30분", "45분", "2시간" 형식
	 * @return 분 단위 시간
	 */
	private int parseTimeToMinutes(String timeStr) {
		try {
			int totalMinutes = 0;

			// "1시간 30분" 또는 "2시간" 형식 처리
			if (timeStr.contains("시간")) {
				String[] parts = timeStr.split("시간");
				// 시간 부분 파싱
				totalMinutes += Integer.parseInt(parts[0].trim()) * 60;

				// 분 부분이 있으면 파싱
				if (parts.length > 1 && parts[1].contains("분")) {
					String minutesPart = parts[1].replace("분", "").trim();
					if (!minutesPart.isEmpty()) {
						totalMinutes += Integer.parseInt(minutesPart);
					}
				}
			} else if (timeStr.contains("분")) {
				// "45분" 형식 처리
				String minutesPart = timeStr.replace("분", "").trim();
				totalMinutes = Integer.parseInt(minutesPart);
			}

			return totalMinutes;
		} catch (NumberFormatException e) {
			log.warn("시간 문자열 파싱 실패: {}", sanitize(timeStr));
			return Integer.MAX_VALUE;
		}
	}

	/**
	 * 두 지점 간 거리 계산 (Haversine formula)
	 * @return 거리 (km)
	 */
	private double calculateDistance(Location from, Location to) {
		if (from == null || to == null) {
			return 0.0;
		}

		final int earthRadiusKm = 6371;

		double lat1 = from.getLatitude();
		double lon1 = from.getLongitude();
		double lat2 = to.getLatitude();
		double lon2 = to.getLongitude();

		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);

		double haversineValue = Math.sin(dLat / 2) * Math.sin(dLat / 2)
			+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
			* Math.sin(dLon / 2) * Math.sin(dLon / 2);

		double centralAngle = 2 * Math.atan2(Math.sqrt(haversineValue), Math.sqrt(1 - haversineValue));

		return earthRadiusKm * centralAngle;
	}

	/**
	 * 거리를 포맷팅된 문자열로 변환
	 * @param distanceKm 거리 (km)
	 * @return 포맷팅된 거리 문자열 (예: "3.5km", "500m")
	 */
	private String formatDistance(double distanceKm) {
		if (distanceKm < 1.0) {
			// 1km 미만은 미터 단위로 표시
			int meters = (int)Math.round(distanceKm * 1000);
			return meters + "m";
		} else {
			// 1km 이상은 km 단위로 표시 (소수점 1자리)
			return String.format("%.1fkm", distanceKm);
		}
	}

	/// 좋아요 누른 공고 목록
	@Override
	@Transactional(readOnly = true)
	public List<NoticeListResponse> getNoticesLike(UUID userId) {

		/// 방 ID 목록 조회
		List<String> noticeIds = likeService.getLikeNoticeIds(userId);

		/// 한번에 조회
		List<NoticeDocument> notices = loadNotices(noticeIds);

		/// DTO 변환
		return notices.stream()
			.map(n -> NoticeListResponse.from(n, true))
			.toList();
	}

	// =================
	//  외부 로직
	// =================

	@Override
	@Transactional
	public NoticeDocument loadNotice(String id) {
		return repository.findById(id)
			.orElseThrow(() -> new CustomException(NoticeErrorCode.NOT_FOUND_NOTICE));
	}

	/// 타입에 따라서 필터링 하기
	@Override
	public List<NoticeDocument> filterNotices(SearchHistory request) {

		// 공급 유형 집합 (한글 기준)
		Set<String> includedSubTypes = request.getSupplyTypes().stream()
			.flatMap(rt -> rt.getIncludedTypes().stream())
			.collect(Collectors.toSet());

		// 타겟 유형 집합
		Set<String> rentalValues = request.getRentalTypes().stream()
			.map(RentalType::getValue)
			.collect(Collectors.toSet());

		// 주택 유형 집합 (한글 기준)
		Set<String> houseTypeValues = request.getHouseType().stream()
			.map(HouseType::getValue)
			.collect(Collectors.toSet());

		return repository.findAll().stream()
			.filter(n -> {
				String ht = Optional.ofNullable(n.getHouseType()).orElse("").trim();
				return houseTypeValues.contains(ht);
			})
			.filter(n -> {
				String st = Optional.ofNullable(n.getSupplyType()).orElse("").trim();
				return includedSubTypes.contains(st);
			})
			.filter(n -> {
				List<String> tgList = Optional.ofNullable(n.getTargetGroups()).orElse(List.of());
				return tgList.stream().anyMatch(rentalValues::contains);
			})
			.toList();
	}

	// =================
	//  내부 로직
	// =================

	/// 아이디 목록에 따른 한번에 엔티티 가져오기
	protected List<NoticeDocument> loadNotices(List<String> noticeIds) {
		return repository.findByIdIn(noticeIds);
	}
}
