package com.pinHouse.server.platform.housing.notice.application.dto;

import com.pinHouse.server.core.util.DateUtil;
import com.pinHouse.server.platform.housing.complex.application.dto.response.ComplexDetailResponse;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import com.pinHouse.server.platform.housing.facility.application.dto.NoticeFacilityListResponse;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 공고 상세 조회 응답 (필터링 적용)
 * filtered: 필터 조건을 만족하는 데이터
 * nonFiltered: 필터 조건을 만족하지 않는 데이터
 */
@Builder
@Schema(name = "[응답][공고] 공고 상세 조회 (필터 적용)", description = "필터링된 데이터와 필터링되지 않은 데이터를 구분하여 반환")
public record NoticeDetailFilteredResponse(
        @Schema(description = "공고 기본 정보")
        NoticeBasicInfo basicInfo,

        @Schema(description = "필터 조건을 만족하는 데이터")
        NoticeDetailData filtered,

        @Schema(description = "필터 조건을 만족하지 않는 데이터")
        NoticeDetailData nonFiltered
) {

    /**
     * 정적 팩토리 메서드 - 필터링 로직 포함
     */
    public static NoticeDetailFilteredResponse from(
            NoticeDocument notice,
            List<ComplexDocument> complexes,
            Map<String, NoticeFacilityListResponse> facilityMap,
            NoticeDetailFilterRequest request
    ) {
        // 1. 기본 정보 생성
        NoticeBasicInfo basicInfo = NoticeBasicInfo.from(notice);

        // 2. 필터링 로직 적용
        List<ComplexDocument> filteredComplexes = new ArrayList<>();
        List<ComplexDocument> nonFilteredComplexes = new ArrayList<>();

        for (ComplexDocument complex : complexes) {
            // Complex 레벨 필터 체크
            NoticeFacilityListResponse facilities = facilityMap.get(complex.getId());
            boolean complexPassesFilter = passesComplexLevelFilters(complex, facilities, request);

            if (!complexPassesFilter) {
                // Complex 자체가 필터를 통과하지 못하면 nonFiltered에 추가
                nonFilteredComplexes.add(complex);
                continue;
            }

            // UnitType 레벨 필터 적용
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

        // 3. 응답 데이터 생성
        NoticeDetailData filtered = NoticeDetailData.from(filteredComplexes, facilityMap);
        NoticeDetailData nonFiltered = NoticeDetailData.from(nonFilteredComplexes, facilityMap);

        return NoticeDetailFilteredResponse.builder()
                .basicInfo(basicInfo)
                .filtered(filtered)
                .nonFiltered(nonFiltered)
                .build();
    }

    /**
     * Complex 레벨 필터 체크 (지역, 인프라 등)
     */
    private static boolean passesComplexLevelFilters(
            ComplexDocument complex,
            NoticeFacilityListResponse facilities,
            NoticeDetailFilterRequest request
    ) {
        // 지역 필터
        if (request.region() != null && !request.region().isEmpty()) {
            String complexRegion = complex.getCity() != null ? complex.getCity() : "";
            if (!request.region().contains(complexRegion)) {
                return false;
            }
        }

        // 인프라 필터 (요청한 facilities가 모두 있어야 함)
        if (request.facilities() != null && !request.facilities().isEmpty()) {
            List<FacilityType> availableFacilities = facilities != null ? facilities.infra() : List.of();

            for (FacilityType requiredFacility : request.facilities()) {
                if (!availableFacilities.contains(requiredFacility)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * UnitType 레벨 필터 체크 (보증금, 월세, 타입코드 등)
     */
    private static boolean passesUnitTypeFilters(UnitType unit, NoticeDetailFilterRequest request) {
        // 보증금 필터
        if (request.minDeposit() > 0 || request.maxDeposit() > 0) {
            long depositTotal = unit.getDeposit() != null ? unit.getDeposit().getTotal() : 0;

            if (request.minDeposit() > 0 && depositTotal < request.minDeposit()) {
                return false;
            }
            if (request.maxDeposit() > 0 && depositTotal > request.maxDeposit()) {
                return false;
            }
        }

        // 월세 필터
        if (request.maxMonthPay() > 0) {
            if (unit.getMonthlyRent() > request.maxMonthPay()) {
                return false;
            }
        }

        // 타입코드 필터
        if (request.typeCode() != null && !request.typeCode().isEmpty()) {
            String unitTypeCode = unit.getTypeCode() != null ? unit.getTypeCode() : "";
            if (!request.typeCode().contains(unitTypeCode)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 공고 기본 정보 (필터와 무관한 정보)
     */
    @Builder
    public record NoticeBasicInfo(
            @Schema(description = "공고ID", example = "101")
            String id,

            @Schema(description = "임대유형", example = "영구임대")
            String type,

            @Schema(description = "주택유형", example = "아파트")
            String housingType,

            @Schema(description = "공급주체", example = "LH")
            String supplier,

            @Schema(description = "공고명", example = "2025 청년 행복주택 공고")
            String name,

            @Schema(description = "모집일정", example = "2025년 10월 ~ 11월")
            String period
    ) {
        public static NoticeBasicInfo from(NoticeDocument notice) {
            String period = DateUtil.formatDate(notice.getApplyStart(), notice.getApplyEnd());
            return NoticeBasicInfo.builder()
                    .id(notice.getId())
                    .name(notice.getTitle())
                    .supplier(notice.getAgency())
                    .period(period)
                    .type(notice.getSupplyType())
                    .housingType(notice.getHouseType())
                    .build();
        }
    }

    /**
     * 필터 적용 데이터 섹션
     */
    @Builder
    public record NoticeDetailData(
            @Schema(description = "해당 섹션의 임대주택 개수", example = "3")
            long totalCount,

            @Schema(description = "임대주택 목록")
            List<ComplexDetailResponse> complexes
    ) {
        public static NoticeDetailData from(
                List<ComplexDocument> complexes,
                Map<String, NoticeFacilityListResponse> facilityMap
        ) {
            List<ComplexDetailResponse> responses = ComplexDetailResponse.from(complexes, facilityMap);
            return NoticeDetailData.builder()
                    .totalCount(responses.size())
                    .complexes(responses)
                    .build();
        }
    }
}
