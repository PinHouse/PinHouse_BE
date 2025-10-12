package com.pinHouse.server.platform.housing.complex.application.dto;

import com.pinHouse.server.platform.housing.complex.domain.entity.Deposit;
import com.pinHouse.server.platform.housing.complex.domain.entity.Quota;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import lombok.Builder;
import java.util.List;

@Builder
public record UnitTypeResponse(
        String typeCode,        // 공급유형 (예: 26A)
        Integer quota,          // 모집호수 정보
        Double exclusiveAreaM2, // 전용면적
        Long deposit,        // 임대보증금
        Integer monthlyRent    // 월임대료(원)
) {

    /// 정적 팩토리 메서드
    public static UnitTypeResponse from(UnitType unitType) {

        Quota typeQuota = unitType.getQuota();
        Deposit typeDeposit = unitType.getDeposit();

        return UnitTypeResponse.builder()
                .typeCode(unitType.getTypeCode())
                .exclusiveAreaM2(unitType.getExclusiveAreaM2())
                .quota(typeQuota.getTotal())
                .deposit(typeDeposit.getTotal())
                .monthlyRent(unitType.getMonthlyRent())
                .build();

    }

    /// 정적 팩토리 메서드
    public static List<UnitTypeResponse> from(List<UnitType> unitTypes) {
        return unitTypes.stream()
                .map(UnitTypeResponse::from)
                .toList();
    }

}
