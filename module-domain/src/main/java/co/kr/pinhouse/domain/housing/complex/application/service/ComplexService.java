package co.kr.pinhouse.domain.housing.complex.application.service;

import static co.kr.pinhouse.common.util.LogSanitizer.sanitize;

import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.pinhouse.common.exception.code.CommonErrorCode;
import co.kr.pinhouse.common.exception.code.ComplexErrorCode;
import co.kr.pinhouse.common.response.CustomException;
import co.kr.pinhouse.domain.Location;
import co.kr.pinhouse.domain.housing.complex.application.dto.response.ComplexDetailResponse;
import co.kr.pinhouse.domain.housing.complex.application.dto.response.DepositMinMaxResponse;
import co.kr.pinhouse.domain.housing.complex.application.dto.response.DepositResponse;
import co.kr.pinhouse.domain.housing.complex.application.dto.response.DistanceResponse;
import co.kr.pinhouse.domain.housing.complex.application.dto.response.TransitInfoResponse;
import co.kr.pinhouse.domain.housing.complex.application.dto.response.TransitRoutesResponse;
import co.kr.pinhouse.domain.housing.complex.application.dto.response.UnitTypeResponse;
import co.kr.pinhouse.domain.housing.complex.application.usecase.ComplexUseCase;
import co.kr.pinhouse.domain.housing.complex.application.util.DistanceCalculator;
import co.kr.pinhouse.domain.housing.complex.application.util.DistanceUtil;
import co.kr.pinhouse.domain.housing.complex.application.util.TransitResponseMapper;
import co.kr.pinhouse.domain.housing.complex.domain.entity.ComplexDocument;
import co.kr.pinhouse.domain.housing.complex.domain.entity.Deposit;
import co.kr.pinhouse.domain.housing.complex.domain.entity.UnitType;
import co.kr.pinhouse.domain.housing.complex.domain.repository.ComplexDocumentRepository;
import co.kr.pinhouse.domain.housing.complex.domain.transit.PathResult;
import co.kr.pinhouse.domain.housing.complex.domain.transit.RootResult;
import co.kr.pinhouse.domain.housing.facility.application.dto.response.NoticeFacilityListResponse;
import co.kr.pinhouse.domain.housing.facility.application.usecase.FacilityUseCase;
import co.kr.pinhouse.domain.like.application.dto.response.UnityTypeLikeResponse;
import co.kr.pinhouse.domain.like.application.usecase.LikeQueryUseCase;
import co.kr.pinhouse.domain.pinpoint.application.usecase.PinPointUseCase;
import co.kr.pinhouse.domain.pinpoint.domain.entity.PinPoint;
import co.kr.pinhouse.domain.search.application.dto.response.ComplexDistanceResponse;
import co.kr.pinhouse.domain.search.domain.entity.SearchHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplexService implements ComplexUseCase {

	// =================
	//  내부 로직
	// =================
	private static final double PYEONG_TO_M2 = 3.305785;
	/// 의존성
	private final ComplexDocumentRepository repository;
	private final PinPointUseCase pinPointService;
	/// 거리 계산 툴
	private final DistanceUtil distanceUtil;
	private final TransitResponseMapper mapper;
	/// 좋아요 목록 조회
	private final LikeQueryUseCase likeService;
	private final FacilityUseCase facilityService;

	// =================
	//  퍼블릭 로직
	// =================
	/// 거리 캐싱
	private final DistanceCacheService distanceCacheService;

	@Override
	@Transactional(readOnly = true)
	public ComplexDetailResponse getComplex(String id, String pinPointId) throws UnsupportedEncodingException {

		/// 조회
		ComplexDocument complex = loadComplex(id);

		/// 주변 인프라 조회
		NoticeFacilityListResponse nearFacilities = facilityService.getNearFacilities(complex.getId());

		/// 상세조회는 상위 1개 경로만 요약한 경량 응답을 사용한다.
		TransitInfoResponse transitInfo = getTransitInfo(id, pinPointId);

		/// 리턴
		return ComplexDetailResponse.from(complex, nearFacilities, transitInfo);

	}

	@Override
	@Transactional(readOnly = true)
	public List<UnitTypeResponse> getComplexUnitTypes(String id, UUID userId) {

		/// 조회
		ComplexDocument complex = loadComplex(id);

		/// 좋아요 상태 조회 (userId가 null이면 빈 목록)
		List<String> likedTypeIds = (userId != null)
			? likeService.getLikeUnitTypeIds(userId)
			: List.of();

		/// 최대 /최소 보증금
		List<UnitType> unitTypes = complex.getUnitTypes();
		return unitTypes.stream()
			.map(unitType -> {
				String typeCode = unitType.getTypeCode();

				// 2. 해당 타입에 대한 최소/최대 보증금 옵션 계산
				DepositResponse depositOptions = getLeaseMinMax(id, typeCode);

				// 3. 좋아요 상태 확인
				boolean isLiked = likedTypeIds.contains(unitType.getTypeId());

				// 4. UnitTypeResponse 생성 및 옵션 주입
				return UnitTypeResponse.from(unitType, depositOptions, isLiked);
			})
			.toList();
	}

	/// 대중교통 전용 API는 상위 3개 경로와 step 정보를 모두 내려준다.
	@Override
	@Transactional
	public TransitRoutesResponse getDistanceV2(String id, String pinPointId) throws UnsupportedEncodingException {
		return calculateTransitRoute(id, pinPointId,
			(pathResult, pinPoint) -> mapper.toTransitRoutesResponse(pathResult, resolveDepartureLabel(pinPoint)));
	}

	// =================
	//  외부 로직
	// =================

	/// 좋아요 누른 방 목록 조회
	@Override
	@Transactional(readOnly = true)
	public List<UnityTypeLikeResponse> getComplexesLikes(UUID userId) {

		/// 방 ID 목록 조회
		List<String> typeIds = likeService.getLikeUnitTypeIds(userId);

		/// ID 목록 바탕으로 조회하기 (하나씩 포함됨)
		List<ComplexDocument> complexDocumentList = loadRooms(typeIds);

		/// DTO 변환하기
		return UnityTypeLikeResponse.from(complexDocumentList);

	}

	/// 상세 조회
	@Override
	@Transactional
	public ComplexDocument loadComplex(String id) {
		return repository.findById(id)
			.orElseThrow(() -> new CustomException(ComplexErrorCode.NOT_FOUND_COMPLEX));
	}

	/// 방 아이디로 조회하기
	@Override
	@Transactional(readOnly = true)
	public ComplexDocument loadComplexByUnitTypeId(String typeId) {

		/// ID 목록으로 방 조회하기
		List<ComplexDocument> results = loadRooms(List.of(typeId));

		if (results.isEmpty()) {
			throw new CustomException(ComplexErrorCode.NOT_FOUND_UNITTYPE);
		}

		return results.getFirst();
	}

	/// 공고 기반 목록 조회
	@Override
	@Transactional
	public List<ComplexDocument> loadComplexes(String noticeId) {
		return repository.findByNoticeId(noticeId);
	}

	/// 공고 기반 목록 조회 (정렬된 유닛타입 포함)
	@Override
	@Transactional(readOnly = true)
	public List<ComplexDocument> loadSortedComplexes(
		String noticeId,
		co.kr.pinhouse.domain.housing.notice.application.dto.UnitTypeSortType sortType
	) {
		log.debug("정렬된 단지 목록 조회 - noticeId: {}, sortType: {}", sanitize(noticeId), sanitize(sortType));
		return repository.findSortedComplexesWithUnitTypes(noticeId, sortType);
	}

	/// 유닛타입 ID 목록으로 단지 목록 조회
	@Override
	@Transactional(readOnly = true)
	public List<ComplexDocument> findComplexesByUnitTypeIds(List<String> typeIds) {
		return repository.findComplexesByUnitTypeIds(typeIds);
	}

	/// 거리 계산 필터링
	@Override
	@Transactional(readOnly = true)
	public List<ComplexDistanceResponse> filterDistanceOnly(List<ComplexDocument> complexDocuments, SearchHistory req) {

		/// 기준 핀포인트 로드
		PinPoint pinPoint = pinPointService.loadPinPoint(req.getPinPointId());
		Location pointLocation = pinPoint.getLocation();

		if (req.getTransitTime() <= 0) {
			throw new CustomException(CommonErrorCode.BAD_PARAMETER);
		}

		/// 반경 계산
		double avgSpeedKmh = 15.0; // 평균 속도 (15km/h)
		double transitTimeMin = req.getTransitTime();
		double distanceKm = (avgSpeedKmh * transitTimeMin) / 60.0;
		double radiusInRadians = distanceKm / 6378.1;

		/// 반경 내 단지 목록
		List<ComplexDocument> nearbyDocs =
			repository.findByLocation(pointLocation.getLongitude(), pointLocation.getLatitude(), radiusInRadians);

		/// 기존 목록과 교집합 + 거리/시간 계산
		return complexDocuments.stream()
			.filter(c -> nearbyDocs.stream().anyMatch(n -> n.getId().equals(c.getId())))
			.map(c -> {
				double km = DistanceCalculator.calculateDistanceKm(pointLocation, c.getLocation());
				int minutes = (int)Math.round((km / avgSpeedKmh) * 60.0); // 평균속도 기반 시간 예측
				return new ComplexDistanceResponse(c, km, minutes);
			})
			.sorted(Comparator.comparingDouble(ComplexDistanceResponse::distanceKm)) // 가까운 순 정렬 (선택)
			.toList();
	}

	/// 필터링
	@Override
	@Transactional(readOnly = true)
	public List<ComplexDistanceResponse> filterUnitTypesOnly(List<ComplexDistanceResponse> complexes,
		SearchHistory req) {

		final double minM2 = toM2(req.getMinSize());
		final double maxM2 = toM2(req.getMaxSize());
		final long maxDeposit = req.getMaxDeposit();
		final long maxMonthlyPay = req.getMaxMonthPay();
		final List<String> rentalTypeValues = req.getRentalTypes() != null
			? req.getRentalTypes().stream()
			.map(co.kr.pinhouse.domain.search.domain.entity.RentalType::getValue)
			.toList()
			: List.of();

		return complexes.stream()
			.filter(cd -> cd != null && cd.complex() != null
				&& cd.complex().getUnitTypes() != null
				&& !cd.complex().getUnitTypes().isEmpty())
			.flatMap(cd -> cd.complex().getUnitTypes().stream()
				.filter(u -> matchesUnitType(u, minM2, maxM2, maxDeposit, maxMonthlyPay, rentalTypeValues))
				.map(u -> {
					ComplexDocument oneUnitDoc = new ComplexDocument(cd.complex(), List.of(u));
					return new ComplexDistanceResponse(oneUnitDoc, cd.distanceKm(), cd.estimatedMinutes());
				})
			)
			.toList();
	}

	/** 평 → m² 변환 */
	private double toM2(double pyeong) {
		if (Double.isNaN(pyeong) || pyeong <= 0) {
			return 0d;
		}
		return pyeong * PYEONG_TO_M2;
	}

	/** 전용면적(m²)/보증금/월임대료/모집대상 필터 함수 */
	private boolean matchesUnitType(UnitType unitType,
		double minM2,
		double maxM2,
		long maxDeposit,
		long maxMonthlyPay,
		List<String> rentalTypeValues) {
		if (unitType == null) {
			return false;
		}

		// 전용면적(m²) 체크
		double areaM2 = unitType.getExclusiveAreaM2();
		if (Double.isNaN(areaM2)) {
			return false;
		}
		if (areaM2 < minM2 || areaM2 > maxM2) {
			return false;
		}

		// 보증금 체크
		Deposit deposit = unitType.getDeposit();
		if (deposit == null) {
			return false;
		}
		long depositTotal = deposit.getTotal();
		if (depositTotal <= 0) {
			return false;
		}
		if (depositTotal > maxDeposit) {
			return false;
		}

		// 월 임대료 체크
		long monthlyRent = unitType.getMonthlyRent();
		if (monthlyRent <= 0) {
			return false;
		}
		if (monthlyRent > maxMonthlyPay) {
			return false;
		}

		// 모집대상(group) 체크
		List<String> group = unitType.getGroup();
		if (group != null && !group.isEmpty() && rentalTypeValues != null && !rentalTypeValues.isEmpty()) {
			// "기본" 또는 "일반"이 포함되어 있으면 무조건 포함
			boolean hasDefaultGroup = group.stream()
				.anyMatch(g -> "기본".equals(g) || "일반".equals(g));

			if (!hasDefaultGroup) {
				// "기본"/"일반"이 없으면, rentalTypes 중 하나라도 group에 포함되어야 함
				boolean hasMatchingRentalType = rentalTypeValues.stream()
					.anyMatch(group::contains);

				if (!hasMatchingRentalType) {
					return false;
				}
			}
		}

		return true;
	}

	/// 유닛 해당하는 임대주택 목록 조회
	@Transactional(readOnly = true)
	protected List<ComplexDocument> loadRooms(List<String> roomIds) {

		/// ObjectId 리스트로 변환
		List<String> typeIdsAsObjectId = roomIds.stream()
			.map(String::new)
			.toList();

		/// 조회 (각 Document는 매칭된 UnitType 1개만 포함)
		return repository.findFirstMatchingUnitType(typeIdsAsObjectId);
	}

	/**
	 * 임대보증금과 월임대료 전환 옵션 계산 메서드
	 *
	 * @param complexId  공고 ID
	 * @param type       공급 유형(예: '전세', '월세')
	 *
	 * <전환 규칙>
	 * - 임대보증금 100만원 단위로만 전환 가능
	 * - 임대보증금 → 월임대료: 연 3.5% 적용 (월이율 = 3.5%/12)
	 * - 월임대료 → 임대보증금: 연 7% 적용 (월이율 = 7%/12)
	 * - 전환 이율 변동 시, 변경된 이율로 재산정
	 */
	@Transactional(readOnly = true)
	public DepositResponse getLeaseMinMax(String complexId, String type) {

		ComplexDocument complex = loadComplex(complexId);
		UnitType unitType = complex.getUnitTypes().stream()
			.filter(info -> info.getTypeCode().equalsIgnoreCase(type))
			.findFirst()
			.orElseThrow(() -> new CustomException(ComplexErrorCode.BAD_REQUEST_DEPOSIT));

		// 기본 보증금 정보
		long balanceBase = unitType.getDeposit().getBalance(); // 잔금 (정책상 변동 없음)
		long contractBase = unitType.getDeposit().getContract(); // 기본 계약금
		long monthRentBase = unitType.getMonthlyRent();          // 기본 월임대료
		long totalDepositBase = Math.max(0, contractBase + balanceBase); // 기본 보증금 총액

		// ===================================
		// 1) 기본 (NORMAL OPTION)
		// ===================================

		DepositMinMaxResponse normalOption = DepositMinMaxResponse.fromWon(
			totalDepositBase,
			contractBase,
			balanceBase,
			monthRentBase
		);

		// 2) 전환 이율 정의
		final double depositToRentAnnualRate = 0.035; // 보증금 감소 이율
		final double rentToDepositAnnualRate = 0.07;  // 보증금 증가 이율

		// ===================================
		// 3) 최소 보증금 / 최대 월세 계산 (MIN OPTION)
		// ===================================

		// 3-1. 최소 보증금: 보통 총 보증금의 50% (100만원 단위 반올림 적용)
		long minRequiredDeposit = (long)Math.round(totalDepositBase * 0.5 / 1_000_000.0) * 1_000_000;

		// 3-2. 최종 전환될 보증금 감소액: (총 보증금 - 최소 보증금)
		long actualDepositReduce = Math.max(0, totalDepositBase - minRequiredDeposit);

		// 100만원 단위로 반올림 (이미 minRequiredDeposit 계산 시 100만원 단위로 맞췄으므로 큰 차이는 없으나, 안전을 위해 최종 금액을 다시 맞춤)
		actualDepositReduce = Math.round(actualDepositReduce / 1_000_000.0) * 1_000_000;

		// 3-3. 증가하는 월세 계산: (감소 보증금) × (연 3.5% / 12)
		long rentIncrease = Math.round(actualDepositReduce * (depositToRentAnnualRate / 12.0));

		// 1천원 단위로 반올림
		long actualRentIncrease = Math.round(rentIncrease / 1_000.0) * 1_000;

		// 최종 결과 (최소 보증금 옵션)
		long minDepositTotal = totalDepositBase - actualDepositReduce;
		long maxMonthRent = monthRentBase + actualRentIncrease;
		long minDepositContract = Math.max(0, minDepositTotal - balanceBase);

		DepositMinMaxResponse minOption = DepositMinMaxResponse.fromWon(
			minDepositTotal,
			minDepositContract,
			balanceBase,
			maxMonthRent
		);

		// ===================================
		// 4) 최대 보증금 / 최소 월세 계산 (MAX OPTION)
		// ===================================

		// 4-1. 최소 월세: 보통 기본 월세의 40% (1천원 단위 반올림 적용)
		long minRequiredRent = Math.max(0, (long)Math.round(monthRentBase * 0.4 / 1_000.0) * 1_000);

		// 4-2. 최종 전환될 월세 감소액: (기본 월세 - 최소 월세)
		long actualRentReduce = Math.max(0, monthRentBase - minRequiredRent);

		// 1천원 단위로 반올림
		actualRentReduce = Math.round(actualRentReduce / 1_000.0) * 1_000;

		// 4-3. 증가하는 보증금 계산: (감소 월세) × (12 / 연 7%)
		long depositIncrease = Math.round(actualRentReduce * (12.0 / rentToDepositAnnualRate));

		// 100만원 단위로 반올림
		long actualDepositIncrease = Math.round(depositIncrease / 1_000_000.0) * 1_000_000;

		// 최종 결과 (최대 보증금 옵션)
		long maxDepositTotal = totalDepositBase + actualDepositIncrease;
		long minMonthRent = monthRentBase - actualRentReduce;
		long maxDepositContract = Math.max(0, maxDepositTotal - balanceBase);

		DepositMinMaxResponse maxOption = DepositMinMaxResponse.fromWon(
			maxDepositTotal,
			maxDepositContract,
			balanceBase,
			minMonthRent
		);

		// 5) 최종 응답 DTO 구성
		return DepositResponse.from(
			minOption,
			normalOption,
			maxOption
		);
	}

	// =================
	//  대중교통 경로 계산 (공통 로직)
	// =================

	/**
	 * 대중교통 경로 계산 템플릿 메서드
	 * 공통 로직(complex/pinpoint 조회, pathResult 계산)을 처리하고,
	 * 결과 변환은 mapper 함수에 위임합니다.
	 *
	 * @param complexId 임대주택 ID
	 * @param pinPointId 핀포인트 ID
	 * @param pathMapper PathResult와 PinPoint를 원하는 타입으로 변환하는 함수
	 * @param <T> 반환 타입
	 * @return 변환된 결과
	 * @throws UnsupportedEncodingException 인코딩 예외
	 */
	private <T> T calculateTransitRoute(
		String complexId,
		String pinPointId,
		BiFunction<PathResult, PinPoint, T> pathMapper
	) throws UnsupportedEncodingException {

		/// 임대주택 조회
		ComplexDocument complex = loadComplex(complexId);
		Location complexLocation = complex.getLocation();

		/// 핀포인트 조회
		PinPoint pinPoint = pinPointService.loadPinPoint(pinPointId);
		Location pinPointLocation = pinPoint.getLocation();

		/// 실제 경로 조회는 ODsay 응답을 PathResult로 표준화한 뒤 후속 매퍼에 위임한다.
		PathResult pathResult = distanceUtil.findPathResult(
			pinPointLocation.getLatitude(),
			pinPointLocation.getLongitude(),
			complexLocation.getLatitude(),
			complexLocation.getLongitude()
		);
		validateTransitRoute(pathResult, complexId, pinPointId);

		/// 결과 매핑
		return pathMapper.apply(pathResult, pinPoint);
	}

	private String resolveDepartureLabel(PinPoint pinPoint) {
		if (pinPoint == null) {
			return "출발지";
		}
		if (hasText(pinPoint.getName())) {
			return pinPoint.getName();
		}
		if (hasText(pinPoint.getAddress())) {
			return pinPoint.getAddress();
		}
		return "출발지";
	}

	private boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	/// 계산 결과에 실제 경로 후보가 있는지 검증
	private void validateTransitRoute(PathResult pathResult, String complexId, String pinPointId) {
		if (pathResult == null || pathResult.routes() == null || pathResult.routes().isEmpty()) {
			log.warn("대중교통 경로를 찾지 못했습니다 - complexId={}, pinPointId={}",
				sanitize(complexId), sanitize(pinPointId));
			throw new CustomException(ComplexErrorCode.NOT_FOUND_TRANSIT_ROUTE);
		}
	}

	/// 임대주택 상세조회용 요약 교통 정보.
	/// 색상 정보를 잃지 않도록 TransitInfoResponse 자체를 캐시한다.
	@Transactional(readOnly = true, noRollbackFor = CustomException.class)
	public TransitInfoResponse getTransitInfo(String id, String pinPointId) throws UnsupportedEncodingException {

		/// 상세조회는 색상/segment 정보가 직렬화된 TransitInfo 캐시를 우선 사용한다.
		TransitInfoResponse cachedTransitInfo = distanceCacheService.getTransitInfo(id, pinPointId);

		if (cachedTransitInfo != null) {
			log.debug("Using cached TransitInfo for complexId={}, pinPointId={}", sanitize(id), sanitize(pinPointId));
			return cachedTransitInfo;
		}

		/// 상세조회 캐시가 없으면 경로를 다시 계산해 색상 포함 응답을 생성한다.
		return calculateTransitRoute(id, pinPointId, (pathResult, pinPoint) -> {
			RootResult rootResult = mapper.selectBest(pathResult);
			TransitInfoResponse transitInfo = mapper.toTransitInfoResponse(rootResult);

			/// 기존 공고/비교 화면 재사용을 위해 RootResult도 함께 캐싱한다.
			distanceCacheService.cacheRootResult(id, pinPointId, rootResult);
			distanceCacheService.cacheTransitInfo(id, pinPointId, transitInfo);

			return transitInfo;
		});
	}

	/// 공고/비교 화면에서 아직 사용하는 구 스키마.
	/// 현재 호출부는 대부분 totalTimeMinutes만 사용하므로, 신규 화면은 getTransitInfo/getDistanceV2로 유지한다.
	@Override
	@Transactional(readOnly = true, noRollbackFor = CustomException.class)
	public DistanceResponse getEasyDistance(String id, String pinPointId) throws UnsupportedEncodingException {

		/// Redis 캐시에서 RootResult 먼저 확인
		co.kr.pinhouse.domain.housing.complex.domain.transit.RootResult cachedRootResult =
			distanceCacheService.getRootResult(id, pinPointId);

		if (cachedRootResult != null) {
			log.debug("Using cached RootResult for complexId={}, pinPointId={}", sanitize(id), sanitize(pinPointId));
			List<DistanceResponse.TransitResponse> routes = mapper.from(cachedRootResult);
			return DistanceResponse.from(cachedRootResult, routes);
		}

		/// 캐시가 없으면 템플릿 메서드를 사용하여 경로 계산
		DistanceResponse distance = calculateTransitRoute(id, pinPointId, (pathResult, pinPoint) -> {
			RootResult rootResult = mapper.selectBest(pathResult);
			TransitInfoResponse transitInfo = mapper.toTransitInfoResponse(rootResult);

			/// RootResult를 Redis에 캐싱
			distanceCacheService.cacheRootResult(id, pinPointId, rootResult);
			distanceCacheService.cacheTransitInfo(id, pinPointId, transitInfo);

			List<DistanceResponse.TransitResponse> routes = mapper.from(rootResult);
			return DistanceResponse.from(rootResult, routes);
		});

		/// 리턴
		return distance;
	}
}
