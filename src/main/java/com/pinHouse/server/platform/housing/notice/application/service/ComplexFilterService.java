package com.pinHouse.server.platform.housing.notice.application.service;

import com.pinHouse.server.platform.Location;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.complex.domain.entity.Deposit;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import com.pinHouse.server.platform.housing.facility.application.dto.NoticeFacilityListResponse;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.housing.notice.application.dto.ComplexFilterResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailFilterRequest;
import com.pinHouse.server.platform.pinPoint.domain.entity.PinPoint;
import com.pinHouse.server.platform.pinPoint.domain.repository.PinPointMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
            NoticeDetailFilterRequest request,
            Map<String, Integer> totalTimeMap
    ) {
        List<ComplexDocument> filteredComplexes = new ArrayList<>();
        List<ComplexDocument> nonFilteredComplexes = new ArrayList<>();

        for (ComplexDocument complex : complexes) {
            // 1. 거리 필터 체크 (최우선) - totalTimeMap의 totalTime 사용
            if (!passesDistanceFilter(complex, request.transitTime(), totalTimeMap)) {
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
     * 거리 필터 체크
     * totalTimeMap의 totalTime을 사용하여 transitTime 이내의 단지만 통과
     */
    private boolean passesDistanceFilter(
            ComplexDocument complex,
            int transitTime,
            Map<String, Integer> totalTimeMap
    ) {
        // transitTime이 0이면 필터링 안함
        if (transitTime <= 0) {
            return true;
        }

        // totalTimeMap이 없거나 비어있으면 모두 통과
        if (totalTimeMap == null || totalTimeMap.isEmpty()) {
            return true;
        }

        // 해당 Complex의 거리 정보 조회
        Integer totalTime = totalTimeMap.get(complex.getId());

        // 거리 정보가 없으면 필터링에서 제외
        if (totalTime == null) {
            log.warn("Distance info not found for complex {}", complex.getId());
            return false;
        }

        // totalTime이 transitTime 이내인지 체크
        return totalTime <= transitTime;
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
        if (request.maxDeposit() > 0) {
            long depositTotal = unit.getDeposit() != null ? unit.getDeposit().getTotal() : 0;

            if (depositTotal > request.maxDeposit()) {
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
     * 단지 목록으로부터 필터 정보 계산
     */
    public ComplexFilterResponse buildFilterResponse(List<ComplexDocument> complexes) {
        ComplexFilterResponse.DistrictFilter districtFilter = calculateDistrictFilter(complexes);
        ComplexFilterResponse.CostFilter costFilter = calculateCostFilter(complexes);
        ComplexFilterResponse.AreaFilter areaFilter = calculateAreaFilter(complexes);

        return ComplexFilterResponse.builder()
                .districtFilter(districtFilter)
                .costFilter(costFilter)
                .areaFilter(areaFilter)
                .build();
    }

    /**
     * 지역 필터 계산
     */
    private ComplexFilterResponse.DistrictFilter calculateDistrictFilter(List<ComplexDocument> complexes) {
        List<ComplexFilterResponse.District> uniqueDistricts = complexes.stream()
                .map(complex -> parseAddress(complex.getCity(), complex.getCounty()))
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.comparing(ComplexFilterResponse.District::city)
                        .thenComparing(ComplexFilterResponse.District::district))
                .toList();

        return ComplexFilterResponse.DistrictFilter.builder()
                .districts(uniqueDistricts)
                .build();
    }

    /**
     * city와 county 필드를 조합하여 District 객체로 변환
     *
     * 한국 주소 체계:
     * - city: "경기도", county: "동두천시" → city: "경기도 동두천시", district: ""
     * - city: "경기도", county: "고양시 일산서구" → city: "경기도 고양시", district: "일산서구"
     * - city: null, county: "서울시 강남구" → city: "서울시", district: "강남구"
     * - city: null, county: "동두천시" → city: "동두천시", district: ""
     */
    private ComplexFilterResponse.District parseAddress(String city, String county) {
        if (county == null || county.isBlank()) {
            return null;
        }

        // county를 공백으로 분리
        String[] countyParts = county.trim().split("\\s+");

        String finalCity;
        String finalDistrict;

        if (countyParts.length >= 2) {
            // county에 "고양시 일산서구" 같은 형식이 있는 경우
            if (city != null && !city.isBlank() && !city.equals(countyParts[0])) {
                // city가 "경기도"이고 county가 "고양시 일산서구"인 경우
                // → city: "경기도 고양시", district: "일산서구"
                finalCity = city + " " + countyParts[0];
                finalDistrict = countyParts[1];
            } else {
                // city가 없거나 county의 첫 부분과 같은 경우
                // county: "서울시 강남구" → city: "서울시", district: "강남구"
                finalCity = countyParts[0];
                finalDistrict = countyParts[1];
            }
        } else {
            // county에 "동두천시" 같은 단일 값만 있는 경우
            if (city != null && !city.isBlank() && !city.equals(county)) {
                // city가 "경기도"이고 county가 "동두천시"인 경우
                // → city: "경기도 동두천시", district: ""
                finalCity = city + " " + county;
                finalDistrict = "";
            } else {
                // city가 없거나 county와 같은 경우
                // county: "동두천시" → city: "동두천시", district: ""
                finalCity = county;
                finalDistrict = "";
            }
        }

        return ComplexFilterResponse.District.builder()
                .city(finalCity)
                .district(finalDistrict)
                .build();
    }

    /**
     * 가격 필터 계산
     */
    private ComplexFilterResponse.CostFilter calculateCostFilter(List<ComplexDocument> complexes) {
        // 모든 unitType의 보증금(deposit.total) 수집
        List<Long> allPrices = complexes.stream()
                .flatMap(complex -> complex.getUnitTypes().stream())
                .map(UnitType::getDeposit)
                .filter(Objects::nonNull)
                .map(Deposit::getTotal)
                .filter(price -> price > 0)
                .toList();

        if (allPrices.isEmpty()) {
            return ComplexFilterResponse.CostFilter.builder()
                    .minPrice(0)
                    .maxPrice(0)
                    .avgPrice(0)
                    .priceDistribution(List.of())
                    .build();
        }

        // 통계 계산
        long minPrice = allPrices.stream().min(Long::compareTo).orElse(0L);
        long maxPrice = allPrices.stream().max(Long::compareTo).orElse(0L);
        long avgPrice = (long) allPrices.stream().mapToLong(Long::longValue).average().orElse(0.0);

        // 가격 분포 계산
        List<ComplexFilterResponse.PriceDistribution> distribution =
                calculatePriceDistribution(allPrices, minPrice, maxPrice);

        return ComplexFilterResponse.CostFilter.builder()
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .avgPrice(avgPrice)
                .priceDistribution(distribution)
                .build();
    }

    /**
     * 가격 분포 계산 (최대 20개 구간)
     */
    private List<ComplexFilterResponse.PriceDistribution> calculatePriceDistribution(
            List<Long> prices,
            long minPrice,
            long maxPrice
    ) {
        if (prices.isEmpty() || minPrice == maxPrice) {
            return List.of(ComplexFilterResponse.PriceDistribution.builder()
                    .rangeStart(minPrice)
                    .rangeEnd(maxPrice)
                    .count(prices.size())
                    .build());
        }

        // 구간 개수 결정 (최대 20개)
        int bucketCount = Math.min(20, prices.size());
        long range = maxPrice - minPrice;
        long bucketSize = Math.max(1, range / bucketCount);

        // 각 구간별 카운트 맵 생성
        Map<Integer, Long> bucketCounts = new HashMap<>();
        for (long price : prices) {
            int bucketIndex = (int) ((price - minPrice) / bucketSize);
            // 최대값이 정확히 maxPrice인 경우 마지막 버킷에 포함
            if (bucketIndex >= bucketCount) {
                bucketIndex = bucketCount - 1;
            }
            bucketCounts.merge(bucketIndex, 1L, Long::sum);
        }

        // PriceDistribution 리스트 생성
        List<ComplexFilterResponse.PriceDistribution> distributions = new ArrayList<>();
        for (int i = 0; i < bucketCount; i++) {
            long rangeStart = minPrice + (i * bucketSize);
            long rangeEnd = (i == bucketCount - 1) ? maxPrice : rangeStart + bucketSize - 1;
            long count = bucketCounts.getOrDefault(i, 0L);

            distributions.add(ComplexFilterResponse.PriceDistribution.builder()
                    .rangeStart(rangeStart)
                    .rangeEnd(rangeEnd)
                    .count(count)
                    .build());
        }

        return distributions;
    }

    /**
     * 면적(타입코드) 필터 계산
     */
    private ComplexFilterResponse.AreaFilter calculateAreaFilter(List<ComplexDocument> complexes) {
        List<String> uniqueTypeCodes = complexes.stream()
                .flatMap(complex -> complex.getUnitTypes().stream())
                .map(UnitType::getTypeCode)
                .filter(Objects::nonNull)
                .filter(code -> !code.isBlank())
                .distinct()
                .sorted()
                .toList();

        return ComplexFilterResponse.AreaFilter.builder()
                .typeCodes(uniqueTypeCodes)
                .build();
    }

    /**
     * 필터 조건에 맞는 단지 개수를 반환
     */
    public int countMatchingComplexes(
            List<ComplexDocument> complexes,
            Map<String, NoticeFacilityListResponse> facilityMap,
            NoticeDetailFilterRequest request,
            Map<String, Integer> totalTimeMap
    ) {
        int count = 0;

        for (ComplexDocument complex : complexes) {
            // 1. 거리 필터 체크 - totalTimeMap의 totalTime 사용
            if (!passesDistanceFilter(complex, request.transitTime(), totalTimeMap)) {
                continue;
            }

            // 2. Complex 레벨 필터 체크 (지역, 인프라)
            NoticeFacilityListResponse facilities = facilityMap.get(complex.getId());
            if (!passesComplexLevelFilters(complex, facilities, request)) {
                continue;
            }

            // 3. UnitType 레벨 필터 체크 - 하나라도 통과하면 카운트
            boolean hasMatchingUnit = complex.getUnitTypes().stream()
                    .anyMatch(unit -> passesUnitTypeFilters(unit, request));

            if (hasMatchingUnit) {
                count++;
            }
        }

        return count;
    }

    /**
     * 필터링 결과를 담는 레코드
     */
    public record FilterResult(
            List<ComplexDocument> filtered,
            List<ComplexDocument> nonFiltered
    ) {}
}
