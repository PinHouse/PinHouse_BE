package com.pinHouse.server.platform.housing.notice.application.service;

import com.pinHouse.server.platform.Location;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import com.pinHouse.server.platform.housing.facility.application.dto.NoticeFacilityListResponse;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailFilterRequest;
import com.pinHouse.server.platform.pinPoint.domain.entity.PinPoint;
import com.pinHouse.server.platform.pinPoint.domain.repository.PinPointMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 공고 상세 조회 필터링 서비스
 * 거리, 지역, 비용, 면적, 주변시설 기반 단지 필터링 담당
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ComplexFilterService {

    private final PinPointMongoRepository pinPointRepository;

    /**
     * 필터 조건에 따라 단지를 filtered와 nonFiltered로 분리
     */
    public FilterResult filterComplexes(
            List<ComplexDocument> complexes,
            Map<String, NoticeFacilityListResponse> facilityMap,
            NoticeDetailFilterRequest request
    ) {
        List<ComplexDocument> filteredComplexes = new ArrayList<>();
        List<ComplexDocument> nonFilteredComplexes = new ArrayList<>();

        // PinPoint 기반 거리 필터링을 위한 위치 정보 조회
        Location userLocation = getUserLocation(request.pinPointId());
        double maxDistanceKm = request.transitTime() > 0 ? request.transitTime() : Double.MAX_VALUE;

        for (ComplexDocument complex : complexes) {
            // 1. 거리 필터 체크 (최우선)
            if (!passesDistanceFilter(complex, userLocation, maxDistanceKm)) {
                nonFilteredComplexes.add(complex);
                continue;
            }

            // 2. Complex 레벨 필터 체크 (지역, 인프라)
            NoticeFacilityListResponse facilities = facilityMap.get(complex.getId());
            if (!passesComplexLevelFilters(complex, facilities, request)) {
                nonFilteredComplexes.add(complex);
                continue;
            }

            // 3. UnitType 레벨 필터 적용 (비용, 면적)
            List<UnitType> allUnitTypes = complex.getUnitTypes();
            List<UnitType> filteredUnits = new ArrayList<>();
            List<UnitType> nonFilteredUnits = new ArrayList<>();

            for (UnitType unit : allUnitTypes) {
                if (passesUnitTypeFilters(unit, request)) {
                    filteredUnits.add(unit);
                } else {
                    nonFilteredUnits.add(unit);
                }
            }

            // 필터를 통과한 UnitType이 있으면 filtered에, 아니면 nonFiltered에
            if (!filteredUnits.isEmpty()) {
                ComplexDocument filteredComplex = ComplexDocument.builder()
                        .src(complex)
                        .unitTypes(filteredUnits)
                        .build();
                filteredComplexes.add(filteredComplex);
            }

            if (!nonFilteredUnits.isEmpty()) {
                ComplexDocument nonFilteredComplex = ComplexDocument.builder()
                        .src(complex)
                        .unitTypes(nonFilteredUnits)
                        .build();
                nonFilteredComplexes.add(nonFilteredComplex);
            }
        }

        return new FilterResult(filteredComplexes, nonFilteredComplexes);
    }

    /**
     * 거리 필터 체크 (MongoDB Geospatial 기반)
     * pinPointId로부터 transitTime(km) 이내의 단지만 통과
     */
    private boolean passesDistanceFilter(
            ComplexDocument complex,
            Location userLocation,
            double maxDistanceKm
    ) {
        // PinPoint가 지정되지 않은 경우 모두 통과
        if (userLocation == null || maxDistanceKm == Double.MAX_VALUE) {
            return true;
        }

        // Complex의 위치 정보 확인
        Location complexLocation = complex.getLocation();
        if (complexLocation == null) {
            log.warn("Complex {} has no location data", complex.getId());
            return false;
        }

        // 거리 계산 (Haversine formula)
        double distanceKm = calculateDistance(
                userLocation.getLatitude(),
                userLocation.getLongitude(),
                complexLocation.getLatitude(),
                complexLocation.getLongitude()
        );

        return distanceKm <= maxDistanceKm;
    }

    /**
     * Complex 레벨 필터 체크 (지역, 인프라)
     */
    private boolean passesComplexLevelFilters(
            ComplexDocument complex,
            NoticeFacilityListResponse facilities,
            NoticeDetailFilterRequest request
    ) {
        // 1. 지역 필터 (county 사용: "성남시 분당구" 형식)
        if (request.region() != null && !request.region().isEmpty()) {
            String complexRegion = complex.getCounty() != null ? complex.getCounty() : "";
            if (!request.region().contains(complexRegion)) {
                log.debug("Complex {} filtered out by region: {} not in {}",
                    complex.getId(), complexRegion, request.region());
                return false;
            }
        }

        // 2. 인프라 필터 (요청한 facilities가 모두 있어야 함)
        if (request.facilities() != null && !request.facilities().isEmpty()) {
            List<FacilityType> availableFacilities = facilities != null ? facilities.infra() : List.of();

            for (FacilityType requiredFacility : request.facilities()) {
                if (!availableFacilities.contains(requiredFacility)) {
                    log.debug("Complex {} filtered out by facility: {} not available",
                        complex.getId(), requiredFacility);
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * UnitType 레벨 필터 체크 (보증금, 월세, 면적 타입코드)
     */
    private boolean passesUnitTypeFilters(UnitType unit, NoticeDetailFilterRequest request) {
        // 1. 보증금 필터
        if (request.minDeposit() > 0 || request.maxDeposit() > 0) {
            long depositTotal = unit.getDeposit() != null ? unit.getDeposit().getTotal() : 0;

            if (request.minDeposit() > 0 && depositTotal < request.minDeposit()) {
                return false;
            }
            if (request.maxDeposit() > 0 && depositTotal > request.maxDeposit()) {
                return false;
            }
        }

        // 2. 월세 필터
        if (request.maxMonthPay() > 0) {
            if (unit.getMonthlyRent() > request.maxMonthPay()) {
                return false;
            }
        }

        // 3. 면적 타입코드 필터
        if (request.typeCode() != null && !request.typeCode().isEmpty()) {
            String unitTypeCode = unit.getTypeCode() != null ? unit.getTypeCode() : "";
            if (!request.typeCode().contains(unitTypeCode)) {
                return false;
            }
        }

        return true;
    }

    /**
     * PinPoint ID로부터 사용자 위치 정보 조회
     */
    private Location getUserLocation(String pinPointId) {
        if (pinPointId == null || pinPointId.isBlank()) {
            return null;
        }

        try {
            PinPoint pinPoint = pinPointRepository.findById(pinPointId)
                    .orElse(null);

            if (pinPoint != null) {
                return pinPoint.getLocation();
            }
        } catch (Exception e) {
            log.error("Failed to fetch PinPoint: {}", pinPointId, e);
        }

        return null;
    }

    /**
     * Haversine formula를 사용한 두 지점 간 거리 계산 (km)
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * 필터링 결과를 담는 레코드
     */
    public record FilterResult(
            List<ComplexDocument> filtered,
            List<ComplexDocument> nonFiltered
    ) {}
}
