package com.pinHouse.server.platform.housing.notice.application.dto;

import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.complex.domain.entity.Deposit;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

/**
 * 유닛타입(방) 비교 응답 DTO
 */
@Builder
@Schema(name = "[응답][공고] 유닛타입 비교", description = "여러 유닛타입의 상세 특징을 비교")
public record UnitTypeCompareResponse(
        @Schema(description = "비교 대상 유닛타입 목록")
        List<UnitTypeComparisonItem> unitTypes
) {

    /**
     * 정적 팩토리 메서드
     */
    public static UnitTypeCompareResponse from(List<UnitTypeComparisonItem> items) {
        return UnitTypeCompareResponse.builder()
                .unitTypes(items)
                .build();
    }

    /**
     * 단일 유닛타입 비교 항목
     */
    @Builder
    @Schema(name = "유닛타입 비교 항목", description = "단일 유닛타입의 모든 특징")
    public record UnitTypeComparisonItem(
            @Schema(description = "유닛타입 ID", example = "type123")
            String typeId,

            @Schema(description = "타입코드", example = "84A")
            String typeCode,

            @Schema(description = "단지 정보")
            ComplexInfo complex,

            @Schema(description = "면적 정보")
            AreaInfo area,

            @Schema(description = "비용 정보")
            CostInfo cost,

            @Schema(description = "주변 인프라 태그", example = "[\"공원\", \"도서관\", \"병원\"]")
            List<FacilityType> nearbyFacilities,

            @Schema(description = "핀포인트 기준 거리", example = "3.5km")
            String distanceFromPinPoint,

            @Schema(description = "좋아요 여부", example = "true")
            boolean isLiked,

            @Schema(description = "공급 그룹 정보", example = "[\"신혼부부\", \"청년\"]")
            List<String> group
    ) {
        /**
         * 정적 팩토리 메서드
         */
        public static UnitTypeComparisonItem from(
                ComplexDocument complex,
                UnitType unitType,
                List<FacilityType> facilities,
                String distance,
                boolean isLiked
        ) {
            return UnitTypeComparisonItem.builder()
                    .typeId(unitType.getTypeId())
                    .typeCode(unitType.getTypeCode())
                    .complex(ComplexInfo.from(complex))
                    .area(AreaInfo.from(unitType))
                    .cost(CostInfo.from(unitType))
                    .nearbyFacilities(facilities != null ? facilities : List.of())
                    .distanceFromPinPoint(distance)
                    .isLiked(isLiked)
                    .group(unitType.getGroup() != null ? unitType.getGroup() : List.of())
                    .build();
        }
    }

    /**
     * 단지 기본 정보
     */
    @Builder
    @Schema(name = "단지 정보", description = "유닛타입이 속한 단지의 기본 정보")
    public record ComplexInfo(
            @Schema(description = "단지 ID", example = "complex123")
            String complexId,

            @Schema(description = "단지명", example = "행복주택 1단지")
            String name,

            @Schema(description = "주소", example = "경기도 성남시 분당구")
            String address

    ) {
        public static ComplexInfo from(ComplexDocument complex) {
            return ComplexInfo.builder()
                    .complexId(complex.getId())
                    .name(complex.getName())
                    .address(complex.getAddress() != null ? complex.getAddress().getFull() : "")
                    .build();
        }
    }

    /**
     * 면적 정보
     */
    @Builder
    @Schema(name = "면적 정보", description = "유닛타입의 면적 정보")
    public record AreaInfo(
            @Schema(description = "전용면적 (m²)", example = "84.5")
            double exclusiveAreaM2,

            @Schema(description = "전용면적 (평)", example = "25.5")
            double exclusiveAreaPyeong
    ) {
        public static AreaInfo from(UnitType unitType) {
            double m2 = unitType.getExclusiveAreaM2();
            double pyeong = m2 * 0.3025; // 1평 = 3.3058m² → 1m² = 0.3025평
            return AreaInfo.builder()
                    .exclusiveAreaM2(Math.round(m2 * 100.0) / 100.0) // 소수점 2자리
                    .exclusiveAreaPyeong(Math.round(pyeong * 100.0) / 100.0) // 소수점 2자리
                    .build();
        }
    }

    /**
     * 비용 정보
     */
    @Builder
    @Schema(name = "비용 정보", description = "유닛타입의 보증금 및 월세 정보")
    public record CostInfo(
            @Schema(description = "보증금 (만원 단위)", example = "5000")
            long totalDeposit,

            @Schema(description = "월 임대료 (원)", example = "300000")
            int monthlyRent
    ) {
        public static CostInfo from(UnitType unitType) {
            Deposit deposit = unitType.getDeposit();
            int monthlyRent = unitType.getMonthlyRent();

            if (deposit == null) {
                return CostInfo.builder()
                        .totalDeposit(0)
                        .monthlyRent(monthlyRent)
                        .build();
            }

            long totalDeposit = deposit.getTotal();
            return CostInfo.builder()
                    .totalDeposit(totalDeposit / 10000)
                    .monthlyRent(monthlyRent)
                    .build();
        }
    }

}
