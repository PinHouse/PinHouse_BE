package com.pinHouse.server.platform.housing.complex.application.service;

import com.pinHouse.server.core.exception.code.CommonErrorCode;
import com.pinHouse.server.core.exception.code.ComplexErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.core.util.DistanceCalculator;
import com.pinHouse.server.platform.Location;
import com.pinHouse.server.platform.housing.complex.application.dto.response.*;
import com.pinHouse.server.platform.housing.complex.application.dto.result.PathResult;
import com.pinHouse.server.platform.housing.complex.application.dto.result.RootResult;
import com.pinHouse.server.platform.housing.complex.application.usecase.ComplexUseCase;
import com.pinHouse.server.platform.housing.complex.application.util.DistanceUtil;
import com.pinHouse.server.platform.housing.complex.application.util.TransitResponseMapper;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.complex.domain.entity.Deposit;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import com.pinHouse.server.platform.housing.complex.domain.repository.ComplexDocumentRepository;
import com.pinHouse.server.platform.housing.facility.application.dto.NoticeFacilityListResponse;
import com.pinHouse.server.platform.housing.facility.application.usecase.FacilityUseCase;
import com.pinHouse.server.platform.like.application.dto.UnityTypeLikeResponse;
import com.pinHouse.server.platform.like.application.usecase.LikeQueryUseCase;
import com.pinHouse.server.platform.pinPoint.application.usecase.PinPointUseCase;
import com.pinHouse.server.platform.pinPoint.domain.entity.PinPoint;
import com.pinHouse.server.platform.search.application.dto.ComplexDistanceResponse;
import com.pinHouse.server.platform.search.application.dto.FastSearchRequest;
import com.pinHouse.server.platform.search.domain.entity.SearchHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplexService implements ComplexUseCase {

    /// 의존성
    private final ComplexDocumentRepository repository;
    private final PinPointUseCase pinPointService;

    /// 거리 계산 툴
    private final DistanceUtil distanceUtil;
    private final TransitResponseMapper mapper;

    /// 좋아요 목록 조회
    private final LikeQueryUseCase likeService;
    private final FacilityUseCase facilityService;

    /// 거리 캐싱
    private final com.pinHouse.server.core.cache.DistanceCacheService distanceCacheService;

    // =================
    //  퍼블릭 로직
    // =================

    @Override
    @Transactional(readOnly = true)
    public ComplexDetailResponse getComplex(String id, String pinPointId) throws UnsupportedEncodingException {

        /// 조회
        ComplexDocument complex = loadComplex(id);

        /// 주변 인프라 조회
        NoticeFacilityListResponse nearFacilities = facilityService.getNearFacilities(complex.getId());

        /// 거리 계산 - Segment 리스트로 변환
        List<TransitRoutesResponse.SegmentResponse> segments = getSegments(id, pinPointId);

        /// 리턴
        return ComplexDetailResponse.from(complex, nearFacilities, segments);

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



    /// 대중교통 시뮬레이터 (새 스키마 - 3개 경로 한 번에)
    @Override
    @Transactional
    public TransitRoutesResponse getDistanceV2(String id, String pinPointId) throws UnsupportedEncodingException {
        return calculateTransitRoute(id, pinPointId, mapper::toTransitRoutesResponse);
    }

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

    // =================
    //  외부 로직
    // =================

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
                    int minutes = (int) Math.round((km / avgSpeedKmh) * 60.0); // 평균속도 기반 시간 예측
                    return new ComplexDistanceResponse(c, km, minutes);
                })
                .sorted(Comparator.comparingDouble(ComplexDistanceResponse::distanceKm)) // 가까운 순 정렬 (선택)
                .toList();
    }


    /// 필터링
    @Override
    @Transactional(readOnly = true)
    public List<ComplexDistanceResponse> filterUnitTypesOnly(List<ComplexDistanceResponse> complexes, SearchHistory req) {

        final double minM2 = toM2(req.getMinSize());
        final double maxM2 = toM2(req.getMaxSize());
        final long   maxDeposit    = req.getMaxDeposit();
        final long   maxMonthlyPay = req.getMaxMonthPay();
        final List<String> rentalTypeValues = req.getRentalTypes() != null
                ? req.getRentalTypes().stream()
                        .map(com.pinHouse.server.platform.search.domain.entity.RentalType::getValue)
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



    // =================
    //  내부 로직
    // =================
    private static final double PYEONG_TO_M2 = 3.305785;

    /** 평 → m² 변환 */
    private double toM2(double pyeong) {
        if (Double.isNaN(pyeong) || pyeong <= 0) return 0d;
        return pyeong * PYEONG_TO_M2;
    }

    /** 전용면적(m²)/보증금/월임대료/모집대상 필터 함수 */
    private boolean matchesUnitType(UnitType u,
                                    double minM2,
                                    double maxM2,
                                    long maxDeposit,
                                    long maxMonthlyPay,
                                    List<String> rentalTypeValues) {
        if (u == null) return false;

        // 전용면적(m²) 체크
        double areaM2 = u.getExclusiveAreaM2();
        if (Double.isNaN(areaM2)) return false;
        if (areaM2 < minM2 || areaM2 > maxM2) return false;

        // 보증금 체크
        Deposit d = u.getDeposit();
        if (d == null) return false;
        long depositTotal = d.getTotal();
        if (depositTotal <= 0) return false;
        if (depositTotal > maxDeposit) return false;

        // 월 임대료 체크
        long monthlyRent = u.getMonthlyRent();
        if (monthlyRent <= 0) return false;
        if (monthlyRent > maxMonthlyPay) return false;

        // 모집대상(group) 체크
        List<String> group = u.getGroup();
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
        long balanceBase      = unitType.getDeposit().getBalance(); // 잔금 (정책상 변동 없음)
        long contractBase     = unitType.getDeposit().getContract(); // 기본 계약금
        long monthRentBase    = unitType.getMonthlyRent();          // 기본 월임대료
        long totalDepositBase = Math.max(0, contractBase + balanceBase); // 기본 보증금 총액

        // ===================================
        // 1) 기본 (NORMAL OPTION)
        // ===================================

        DepositMinMaxResponse normalOption = new DepositMinMaxResponse(
                totalDepositBase,
                contractBase,
                balanceBase,
                monthRentBase
        );

        // 2) 전환 이율 정의
        final double DEPOSIT_TO_RENT_ANNUAL_RATE = 0.035; // 보증금 감소 이율
        final double RENT_TO_DEPOSIT_ANNUAL_RATE = 0.07;  // 보증금 증가 이율

        // ===================================
        // 3) 최소 보증금 / 최대 월세 계산 (MIN OPTION)
        // ===================================

        // 3-1. 최소 보증금: 보통 총 보증금의 50% (100만원 단위 반올림 적용)
        long minRequiredDeposit = (long) Math.round(totalDepositBase * 0.5 / 1_000_000.0) * 1_000_000;

        // 3-2. 최종 전환될 보증금 감소액: (총 보증금 - 최소 보증금)
        long actualDepositReduce = Math.max(0, totalDepositBase - minRequiredDeposit);

        // 100만원 단위로 반올림 (이미 minRequiredDeposit 계산 시 100만원 단위로 맞췄으므로 큰 차이는 없으나, 안전을 위해 최종 금액을 다시 맞춤)
        actualDepositReduce = Math.round(actualDepositReduce / 1_000_000.0) * 1_000_000;

        // 3-3. 증가하는 월세 계산: (감소 보증금) × (연 3.5% / 12)
        long rentIncrease = Math.round(actualDepositReduce * (DEPOSIT_TO_RENT_ANNUAL_RATE / 12.0));

        // 1천원 단위로 반올림
        long actualRentIncrease = Math.round(rentIncrease / 1_000.0) * 1_000;

        // 최종 결과 (최소 보증금 옵션)
        long minDepositTotal = totalDepositBase - actualDepositReduce;
        long maxMonthRent  = monthRentBase + actualRentIncrease;
        long minDepositContract = Math.max(0, minDepositTotal - balanceBase);

        DepositMinMaxResponse minOption = new DepositMinMaxResponse(
                minDepositTotal,
                minDepositContract,
                balanceBase,
                maxMonthRent
        );

        // ===================================
        // 4) 최대 보증금 / 최소 월세 계산 (MAX OPTION)
        // ===================================

        // 4-1. 최소 월세: 보통 기본 월세의 40% (1천원 단위 반올림 적용)
        long minRequiredRent = Math.max(0, (long) Math.round(monthRentBase * 0.4 / 1_000.0) * 1_000);

        // 4-2. 최종 전환될 월세 감소액: (기본 월세 - 최소 월세)
        long actualRentReduce = Math.max(0, monthRentBase - minRequiredRent);

        // 1천원 단위로 반올림
        actualRentReduce = Math.round(actualRentReduce / 1_000.0) * 1_000;

        // 4-3. 증가하는 보증금 계산: (감소 월세) × (12 / 연 7%)
        long depositIncrease = Math.round(actualRentReduce * (12.0 / RENT_TO_DEPOSIT_ANNUAL_RATE));

        // 100만원 단위로 반올림
        long actualDepositIncrease = Math.round(depositIncrease / 1_000_000.0) * 1_000_000;

        // 최종 결과 (최대 보증금 옵션)
        long maxDepositTotal = totalDepositBase + actualDepositIncrease;
        long minMonthRent  = monthRentBase - actualRentReduce;
        long maxDepositContract = Math.max(0, maxDepositTotal - balanceBase);

        DepositMinMaxResponse maxOption = new DepositMinMaxResponse(
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
     * @param pathMapper PathResult를 원하는 타입으로 변환하는 함수
     * @param <T> 반환 타입
     * @return 변환된 결과
     * @throws UnsupportedEncodingException 인코딩 예외
     */
    private <T> T calculateTransitRoute(
            String complexId,
            String pinPointId,
            java.util.function.Function<PathResult, T> pathMapper
    ) throws UnsupportedEncodingException {

        /// 임대주택 조회
        ComplexDocument complex = loadComplex(complexId);
        Location complexLocation = complex.getLocation();

        /// 핀포인트 조회
        PinPoint pinPoint = pinPointService.loadPinPoint(pinPointId);
        Location pinPointLocation = pinPoint.getLocation();

        /// 대중교통 경로 계산
        PathResult pathResult = distanceUtil.findPathResult(
                pinPointLocation.getLatitude(),
                pinPointLocation.getLongitude(),
                complexLocation.getLatitude(),
                complexLocation.getLongitude()
        );

        /// 결과 매핑
        return pathMapper.apply(pathResult);
    }

    /// Segment 리스트 조회 (임대주택 상세조회용)
    @Transactional(readOnly = true)
    public List<TransitRoutesResponse.SegmentResponse> getSegments(String id, String pinPointId) throws UnsupportedEncodingException {
        return calculateTransitRoute(id, pinPointId, pathResult -> {
            RootResult rootResult = mapper.selectBest(pathResult);
            return mapper.toSegmentResponses(rootResult);
        });
    }

    /// 간편 대중교통 시뮬레이터
    @Override
    @Transactional(readOnly = true)
    public DistanceResponse getEasyDistance(String id, String pinPointId) throws UnsupportedEncodingException {

        /// Redis 캐시 먼저 확인
        DistanceResponse cached = distanceCacheService.getDistance(id, pinPointId);
        if (cached != null) {
            log.debug("Using cached distance for complexId={}, pinPointId={}", id, pinPointId);
            return cached;
        }

        /// 템플릿 메서드를 사용하여 경로 계산
        DistanceResponse distance = calculateTransitRoute(id, pinPointId, pathResult -> {
            RootResult rootResult = mapper.selectBest(pathResult);
            List<DistanceResponse.TransitResponse> routes = mapper.from(rootResult);
            return DistanceResponse.from(rootResult, routes);
        });

        /// Redis에 캐싱
        distanceCacheService.cacheDistance(id, pinPointId, distance);

        /// 리턴
        return distance;
    }
}
