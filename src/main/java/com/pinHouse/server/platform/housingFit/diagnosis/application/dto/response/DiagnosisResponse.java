package com.pinHouse.server.platform.housingFit.diagnosis.application.dto.response;

import lombok.*;

import java.util.Map;

/**
 * 진단 결과 DTO
 */
@Builder
public record DiagnosisResponse(
        boolean eligible,
        String supplyTypeCode,
        String displayName,
        int score,
        Map<String, Object> meta
        ) {

    /// 정적 팩토리 메서드
    public static DiagnosisResponse of(boolean eligible, String supplyTypeCode, String displayName) {
        return DiagnosisResponse.builder()
                .eligible(eligible)
                .supplyTypeCode(supplyTypeCode)
                .displayName(displayName)
                .build();
    }

}
