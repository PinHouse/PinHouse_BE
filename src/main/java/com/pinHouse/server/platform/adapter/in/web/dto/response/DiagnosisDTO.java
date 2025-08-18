package com.pinHouse.server.platform.adapter.in.web.dto.response;

import com.pinHouse.server.platform.domain.diagnosis.entity.Diagnosis;
import com.pinHouse.server.platform.domain.diagnosis.model.DiagnosisResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * 청약진단에 따른 결과를 주는 DTO 입니다.
 * @param eligible      가능 여부
 * @param supplyType    추천 공급 여부
 * @param reason        근거
 */
@Builder
public record DiagnosisDTO(
        boolean eligible,
        String supplyType,
        String reason) {


    /// 정적 팩토리 메서드
    public static DiagnosisDTO from(DiagnosisResult result) {
        return DiagnosisDTO.builder()
                .eligible(result.eligible())
                .supplyType(result.supplyType())
                .reason(result.reason())
                .build();
    }

    @Builder
    public record Reason(
            String code,
            String message,
            Map<String, Object> data,
            boolean pass,
            String severity

    ) {
    }

}

