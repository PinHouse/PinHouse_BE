package com.pinHouse.server.platform.search.application.dto;

import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record FastUnitTypeResponse(

        String complexId,                   // 공고 ID
        String complexName,                 // 단지명
        String typeCode,                    // 방 유형
        String typeId,                      // 방 ID
        String heating,                     // 난방방식

        @Schema(description = "보증금 (만원 단위)", example = "5000")
        long deposit,

        @Schema(description = "월 임대료 (원)", example = "300000")
        long monthPayment,

        double size,                        // 면적
        Integer totalSupplyInNotice,        // 공급호수합계

        @Schema(description = "평균 시간 (단위: 분)", example = "35.5")
        double averageTime,

        @Schema(description = "KM", example = "15.6")
        double km,

        List<String> infra,            // 인프라 종류

        @Schema(description = "좋아요 여부", example = "true")
        boolean liked,                 // 좋아요 여부

        @Schema(description = "모집 대상 그룹", example = "[\"신혼부부\", \"청년\"]")
        List<String> group             // 모집 대상 그룹
) {


    public static FastUnitTypeResponse from(ComplexDistanceResponse complexDistanceResponse, List<FacilityType> facilityTypes, boolean liked) {

        /// 아파트
        ComplexDocument complexDocument = complexDistanceResponse.complex();

        /// 1개씩만 있기에
        UnitType unitType = complexDocument.getUnitTypes().getFirst();

        return FastUnitTypeResponse.builder()
                .typeId(unitType.getTypeId())
                .complexName(complexDocument.getName())
                .typeCode(unitType.getTypeCode())
                .heating(complexDocument.getHeating())
                .deposit(unitType.getDeposit().getTotal() / 10000)
                .monthPayment(unitType.getMonthlyRent())
                .size(unitType.getExclusiveAreaM2())
                .totalSupplyInNotice(unitType.getQuota().getTotal())
                .complexId(complexDocument.getId())
                .averageTime(complexDistanceResponse.estimatedMinutes())
                .km(complexDistanceResponse.distanceKm())
                .infra(facilityTypes.stream()
                        .map(FacilityType::getValue)
                        .toList())
                .liked(liked)
                .group(unitType.getGroup())
                .build();

    }

    /// 정적 팩토리 메서드 (오버로드 - 좋아요 정보 없을 때)
    public static FastUnitTypeResponse from(ComplexDistanceResponse complexDistanceResponse, List<FacilityType> facilityTypes) {
        return from(complexDistanceResponse, facilityTypes, false);
    }

}
