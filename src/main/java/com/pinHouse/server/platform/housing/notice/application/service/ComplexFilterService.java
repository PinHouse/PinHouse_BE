package com.pinHouse.server.platform.housing.notice.application.service;

import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.complex.domain.entity.Deposit;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import com.pinHouse.server.platform.housing.facility.application.dto.NoticeFacilityListResponse;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.housing.notice.application.dto.ComplexFilterResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailFilterRequest;
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
    public ComplexFilterResponse.DistrictFilter calculateDistrictFilter(List<ComplexDocument> complexes) {
        // 1. 각 complex에서 city와 district 추출
        List<TempDistrictInfo> tempDistricts = complexes.stream()
                .map(this::parseAddress)
                .filter(Objects::nonNull)
                .toList();

        // 2. city별로 그룹화하여 district 목록 생성
        Map<String, List<String>> cityToDistricts = tempDistricts.stream()
                .collect(Collectors.groupingBy(
                        TempDistrictInfo::city,
                        Collectors.mapping(
                                TempDistrictInfo::district,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        list -> list.stream().distinct().sorted().toList()
                                )
                        )
                ));

        // 3. 최종 District 리스트 생성
        List<ComplexFilterResponse.District> groupedDistricts = cityToDistricts.entrySet().stream()
                .map(entry -> ComplexFilterResponse.District.builder()
                        .city(entry.getKey())
                        .districts(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(ComplexFilterResponse.District::city))
                .toList();

        return ComplexFilterResponse.DistrictFilter.builder()
                .districts(groupedDistricts)
                .build();
    }

    /**
     * 임시 District 정보 (그룹화 전)
     */
    private record TempDistrictInfo(String city, String district) {}

    /**
     * ComplexDocument의 주소 정보를 파싱하여 TempDistrictInfo로 변환
     *
     * 변환 규칙:
     * 1. 광역시/특별시: "부산시 해운대구" → city: "부산", district: "해운대구"
     * 2. 일반시 (구 있음): city: "경기도", county: "청주시 서원구" → city: "경기", district: "청주시 서원구"
     * 3. 일반시 (구 없음): city: "경기도", county: "동두천시" → city: "경기", district: "동두천시"
     */
    private TempDistrictInfo parseAddress(ComplexDocument complex) {
        String county = complex.getCounty();
        String city = complex.getCity();

        if (county == null || county.isBlank()) {
            return null;
        }

        // 광역시 및 특별시 목록
        final Set<String> METRO_CITIES = Set.of(
                "서울", "부산", "대구", "인천", "광주", "대전", "울산", "세종"
        );

        // county를 공백으로 분리
        String[] countyParts = county.trim().split("\\s+");

        String finalCity;
        String finalDistrict;

        // 광역시/특별시 여부 확인
        boolean isMetroCity = false;
        String metroCityName = null;

        if (countyParts.length >= 1) {
            String cityName = countyParts[0];
            // "광주광역시", "부산광역시", "서울특별시" 등에서 도시 이름 추출
            String cityBase = extractMetroCityName(cityName);
            if (cityBase != null && METRO_CITIES.contains(cityBase)) {
                isMetroCity = true;
                metroCityName = cityBase;
            }
        }

        if (isMetroCity) {
            // 광역시/특별시인 경우
            finalCity = metroCityName;

            if (countyParts.length >= 2) {
                // "부산시 해운대구" 또는 "광주광역시 서구" → city: "부산", district: "해운대구"
                finalDistrict = countyParts[1];
            } else {
                // "대구광역시"만 있는 경우 → city: "대구", district: "대구광역시" (원본 유지)
                finalDistrict = county;
            }
        } else {
            // 일반시인 경우
            // city 필드가 광역시일 수도 있으므로 확인
            String cityBase = extractMetroCityName(city);
            if (cityBase != null && METRO_CITIES.contains(cityBase)) {
                // city 필드가 "대구광역시"인 경우 → city: "대구"
                finalCity = cityBase;
            } else {
                // city 필드가 도인 경우 → "경기도" → "경기"
                finalCity = shortenProvinceName(city);
            }
            finalDistrict = county;
        }

        return new TempDistrictInfo(finalCity, finalDistrict);
    }

    /**
     * 광역시/특별시 이름 추출
     * "광주광역시" → "광주"
     * "부산광역시" → "부산"
     * "서울특별시" → "서울"
     * "부산시" → "부산"
     */
    private String extractMetroCityName(String cityName) {
        if (cityName == null || cityName.isBlank()) {
            return null;
        }

        // "광역시" 제거
        if (cityName.endsWith("광역시")) {
            return cityName.substring(0, cityName.length() - 3);
        }

        // "특별시" 제거
        if (cityName.endsWith("특별시")) {
            return cityName.substring(0, cityName.length() - 3);
        }

        // "특별자치시" 제거 (세종)
        if (cityName.endsWith("특별자치시")) {
            return cityName.substring(0, cityName.length() - 5);
        }

        // "시" 제거
        if (cityName.endsWith("시")) {
            return cityName.substring(0, cityName.length() - 1);
        }

        return cityName;
    }

    /**
     * 도 이름 축약
     * "경기도" → "경기"
     * "충청북도" → "충북"
     * "경상남도" → "경남"
     * "제주특별자치도" → "제주"
     */
    private String shortenProvinceName(String province) {
        if (province == null || province.isBlank()) {
            return "";
        }

        // "특별자치도" 제거 (제주)
        if (province.endsWith("특별자치도")) {
            return province.substring(0, province.length() - 5);
        }

        // "도" 제거
        if (province.endsWith("도")) {
            String base = province.substring(0, province.length() - 1);

            // "충청북", "충청남", "경상북", "경상남", "전라북", "전라남" → 첫글자 + 마지막글자
            if (base.startsWith("충청") || base.startsWith("경상") || base.startsWith("전라")) {
                // "충청북" → "충북", "경상남" → "경남"
                return base.charAt(0) + String.valueOf(base.charAt(base.length() - 1));
            }

            // 그 외 도는 "도"만 제거 ("경기도" → "경기", "강원도" → "강원")
            return base;
        }

        return province;
    }

    /**
     * 가격 필터 계산
     */
    public ComplexFilterResponse.CostFilter calculateCostFilter(List<ComplexDocument> complexes) {
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
                    .build();
        }

        // 통계 계산 (원 단위)
        long minPrice = allPrices.stream().min(Long::compareTo).orElse(0L);
        long maxPrice = allPrices.stream().max(Long::compareTo).orElse(0L);
        long avgPrice = (long) allPrices.stream().mapToLong(Long::longValue).average().orElse(0.0);

        // 만 단위로 변환하여 반환
        return ComplexFilterResponse.CostFilter.builder()
                .minPrice(minPrice / 10000)
                .maxPrice(maxPrice / 10000)
                .avgPrice(avgPrice / 10000)
                .build();
    }

    /**
     * 면적(타입코드) 필터 계산
     */
    public ComplexFilterResponse.AreaFilter calculateAreaFilter(List<ComplexDocument> complexes) {
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
