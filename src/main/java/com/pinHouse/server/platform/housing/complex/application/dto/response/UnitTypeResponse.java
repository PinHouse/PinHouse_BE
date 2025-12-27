package com.pinHouse.server.platform.housing.complex.application.dto.response;

import com.pinHouse.server.platform.housing.complex.domain.entity.Quota;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import lombok.Builder;

import java.util.List;

@Builder
public record UnitTypeResponse(
        String typeId,
        String typeCode,        // 공급유형 (예: 26A)
        String thumbnail,
        Integer quota,          // 모집호수 정보
        Double exclusiveAreaM2, // 전용면적
        DepositResponse deposit,    /// 최대/보통/최소
        boolean liked,               ///  좋아요
        List<String> group           /// 공급 그룹 정보
) {

    /// 정적 팩토리 메서드
    public static UnitTypeResponse from(UnitType unitType, DepositResponse deposit, boolean liked) {

        Quota typeQuota = unitType.getQuota();

        return UnitTypeResponse.builder()
                .typeId(unitType.getTypeId())
                .typeCode(unitType.getTypeCode())
                .thumbnail(null)
                .exclusiveAreaM2(unitType.getExclusiveAreaM2())
                .deposit(deposit)
                .quota(typeQuota.getTotal())
                .liked(liked)
                .group(unitType.getGroup())
                .build();

    }

    /// 정적 팩토리 메서드 (오버로드 - 좋아요 정보 없을 때)
    public static UnitTypeResponse from(UnitType unitType, DepositResponse deposit) {
        return from(unitType, deposit, false);
    }
}
