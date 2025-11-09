package com.pinHouse.server.platform.housing.complex.application.dto.response;

import com.pinHouse.server.platform.housing.complex.domain.entity.Quota;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import lombok.Builder;

@Builder
public record UnitTypeResponse(
        String typeCode,        // 공급유형 (예: 26A)
        String thumbnail,
        Integer quota,          // 모집호수 정보
        Double exclusiveAreaM2, // 전용면적
        DepositResponse deposit,    /// 최대/보통/최소
        boolean liked               ///  좋아요
) {

    /// 정적 팩토리 메서드
    public static UnitTypeResponse from(UnitType unitType, DepositResponse deposit) {

        Quota typeQuota = unitType.getQuota();

        return UnitTypeResponse.builder()
                .typeCode(unitType.getTypeCode())
                .thumbnail(null)
                .exclusiveAreaM2(unitType.getExclusiveAreaM2())
                .deposit(deposit)
                .quota(typeQuota.getTotal())
                .liked(false)
                .build();

    }
}
