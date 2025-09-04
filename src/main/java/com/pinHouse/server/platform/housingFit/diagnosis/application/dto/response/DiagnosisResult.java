package com.pinHouse.server.platform.housingFit.diagnosis.application.dto.response;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 진단 결과 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisResult {

    private boolean eligible;           // 자격 여부
    private String supplyTypeCode;      // 공급유형 코드 (YOUTH_SPECIAL 등)
    private String displayName;         // UI 표시명 ("청년 특별공급")
    private int score;                  // 가점/점수

    private Map<String, Object> meta = new HashMap<>();;                 // 후보군, 점수 내역 등 추가 메타정보

    // 팩토리 메서드
    public static DiagnosisResult of(boolean eligible, String supplyTypeCode, String displayName) {
        return DiagnosisResult.builder()
                .eligible(eligible)
                .supplyTypeCode(supplyTypeCode)
                .displayName(displayName)
                .build();
    }


    public void addMeta(Map<String, Object> meta) {
        this.meta.putAll(meta);
    }

    public void addScore(int score) {
        this.score = score;
    }
}
