package com.pinHouse.server.platform.housing.notice.application.service;

import com.pinHouse.server.core.exception.code.NoticeErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.core.response.response.pageable.SliceRequest;
import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.Location;
import com.pinHouse.server.platform.housing.complex.application.usecase.ComplexUseCase;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.facility.application.dto.NoticeFacilityListResponse;
import com.pinHouse.server.platform.housing.facility.application.usecase.FacilityUseCase;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.housing.notice.application.dto.ComplexFilterResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailFilterRequest;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailFilteredResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListRequest;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.UnitTypeCompareResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.UnitTypeSortType;
import com.pinHouse.server.platform.housing.notice.application.usecase.NoticeUseCase;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import com.pinHouse.server.platform.housing.notice.domain.repository.NoticeDocumentRepository;
import com.pinHouse.server.platform.like.application.usecase.LikeQueryUseCase;
import com.pinHouse.server.platform.pinPoint.application.usecase.PinPointUseCase;
import com.pinHouse.server.platform.pinPoint.domain.entity.PinPoint;
import com.pinHouse.server.platform.search.domain.entity.HouseType;
import com.pinHouse.server.platform.search.domain.entity.RentalType;
import com.pinHouse.server.platform.search.domain.entity.SearchHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    // =================
    //  퍼블릭 로직
    // =================
    @Override
    public SliceResponse<NoticeListResponse> getNotices(NoticeListRequest request, SliceRequest sliceRequest, UUID userId) {

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

        /// 서비스 레이어에서 필터링 수행
        ComplexFilterService.FilterResult filterResult =
                complexFilterService.filterComplexes(complexes, facilityMap, request);

        /// DTO 정적 팩토리 메서드로 응답 생성 (이미 필터링된 데이터 전달)
        return NoticeDetailFilteredResponse.from(
                notice,
                filterResult.filtered(),
                filterResult.nonFiltered(),
                facilityMap
        );
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

    /// 유닛타입(방) 비교
    @Override
    @Transactional(readOnly = true)
    public UnitTypeCompareResponse compareUnitTypes(String noticeId, String pinPointId, UnitTypeSortType sortType) {

        /// 공고 존재 확인
        loadNotice(noticeId);

        /// 공고에 속한 모든 단지 조회
        List<ComplexDocument> complexes = complexService.loadComplexes(noticeId);

        /// PinPoint 위치 조회 (optional)
        Location userLocation = null;
        if (pinPointId != null && !pinPointId.isBlank()) {
            try {
                PinPoint pinPoint = pinPointService.loadPinPoint(pinPointId);
                userLocation = pinPoint.getLocation();
            } catch (Exception e) {
                log.warn("Failed to load PinPoint: {}", pinPointId, e);
            }
        }

        /// 각 단지의 시설 정보 조회
        Map<String, List<FacilityType>> facilityMap = complexes.stream()
                .collect(Collectors.toMap(
                        ComplexDocument::getId,
                        complex -> {
                            NoticeFacilityListResponse facilityResponse = facilityService.getNearFacilities(complex.getId());
                            return facilityResponse != null ? facilityResponse.infra() : List.of();
                        }
                ));

        /// 각 단지의 거리 계산 (userLocation이 있는 경우에만)
        Map<String, String> distanceMap = new HashMap<>();
        if (userLocation != null) {
            Location finalUserLocation = userLocation;
            distanceMap = complexes.stream()
                    .collect(Collectors.toMap(
                            ComplexDocument::getId,
                            complex -> formatDistance(calculateDistance(finalUserLocation, complex.getLocation()))
                    ));
        }

        /// 최종 거리 맵 (람다에서 사용하기 위해 effectively final)
        Map<String, String> finalDistanceMap = distanceMap;

        /// 모든 단지의 유닛타입을 수집하여 비교 항목 생성
        List<UnitTypeCompareResponse.UnitTypeComparisonItem> comparisonItems = complexes.stream()
                .flatMap(complex -> {
                    String complexId = complex.getId();
                    List<FacilityType> facilities = facilityMap.getOrDefault(complexId, List.of());
                    String distance = finalDistanceMap.getOrDefault(complexId, null);

                    return complex.getUnitTypes().stream()
                            .map(unitType -> UnitTypeCompareResponse.UnitTypeComparisonItem.from(
                                    complex, unitType, facilities, distance
                            ));
                })
                .collect(Collectors.toList());

        /// 정렬 기준 적용 (null이면 기본값)
        UnitTypeSortType finalSortType = sortType != null ? sortType : UnitTypeSortType.DEPOSIT_ASC;
        sortUnitTypes(comparisonItems, finalSortType);

        /// DTO 정적 팩토리 메서드로 응답 생성
        return UnitTypeCompareResponse.from(comparisonItems);
    }

    /**
     * 유닛타입 정렬
     */
    private void sortUnitTypes(
            List<UnitTypeCompareResponse.UnitTypeComparisonItem> items,
            UnitTypeSortType sortType
    ) {
        switch (sortType) {
            case DEPOSIT_ASC:
                // 보증금 낮은 순 (null은 최대값으로 처리)
                items.sort(Comparator.comparing(
                        item -> item.cost() != null ? item.cost().totalDeposit() : Long.MAX_VALUE
                ));
                break;

            case AREA_DESC:
                // 평수 넓은 순 (null은 최소값으로 처리)
                items.sort(Comparator.comparing(
                        (UnitTypeCompareResponse.UnitTypeComparisonItem item) ->
                                item.area() != null ? item.area().exclusiveAreaM2() : 0.0
                ).reversed());
                break;
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

        final int EARTH_RADIUS_KM = 6371;

        double lat1 = from.getLatitude();
        double lon1 = from.getLongitude();
        double lat2 = to.getLatitude();
        double lon2 = to.getLongitude();

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * 거리를 포맷팅된 문자열로 변환
     * @param distanceKm 거리 (km)
     * @return 포맷팅된 거리 문자열 (예: "3.5km", "500m")
     */
    private String formatDistance(double distanceKm) {
        if (distanceKm < 1.0) {
            // 1km 미만은 미터 단위로 표시
            int meters = (int) Math.round(distanceKm * 1000);
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
