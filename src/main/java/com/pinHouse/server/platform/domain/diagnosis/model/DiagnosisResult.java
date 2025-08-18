package com.pinHouse.server.platform.domain.diagnosis.model;

import lombok.Builder;

@Builder
public record DiagnosisResult(
        boolean eligible,
        String supplyType,
        String reason
) {

    /// 정적 팩토리 메서드
    public static DiagnosisResult of(boolean eligible, String supplyType, String reason) {
        return DiagnosisResult.builder()
                .eligible(eligible)
                .supplyType(supplyType)
                .reason(reason)
                .build();
    }

}
