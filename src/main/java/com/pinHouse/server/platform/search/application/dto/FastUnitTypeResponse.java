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
        long deposit,                       // 보증금
        long monthPayment,                  // 월임대료
        double size,                        // 면적
        Integer totalSupplyInNotice,        // 공급호수합계

        @Schema(description = "평균 시간 (단위: 분)", example = "35.5")
        double averageTime,

        @Schema(description = "KM", example = "15.6")
        double km,

        List<String> infra            // 인프라 종류
) {


    public static FastUnitTypeResponse from(ComplexDistanceResponse complexDistanceResponse, List<FacilityType> facilityTypes) {

        /// 아파트
        ComplexDocument complexDocument = complexDistanceResponse.complex();

        /// 1개씩만 있기에
        UnitType unitType = complexDocument.getUnitTypes().getFirst();

        return FastUnitTypeResponse.builder()
                .typeId(unitType.getTypeId())
                .complexName(complexDocument.getName())
                .typeCode(unitType.getTypeCode())
                .heating(complexDocument.getHeating())
                .deposit(unitType.getDeposit().getTotal())
                .monthPayment(unitType.getMonthlyRent())
                .size(unitType.getExclusiveAreaM2())
                .totalSupplyInNotice(unitType.getQuota().getTotal())
                .complexId(complexDocument.getComplexKey())
                .averageTime(complexDistanceResponse.estimatedMinutes())
                .km(complexDistanceResponse.distanceKm())
                .infra(facilityTypes.stream()
                        .map(FacilityType::getValue)
                        .toList())
                .build();

    }

}
