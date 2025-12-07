package com.pinHouse.server.platform.diagnostic.diagnosis.application.dto;

import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.Diagnosis;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 진단 히스토리 요약 응답 DTO
 * - 진단 기록 목록 조회 시 사용
 */
@Schema(name = "[응답][진단] 진단 히스토리 요약", description = "진단 히스토리 목록을 응답하는 DTO입니다.")
@Builder
public record DiagnosisHistoryResponse(

        @Schema(description = "진단 기록 ID", example = "101")
        Long id,

        @Schema(description = "진단 일시", example = "2025-12-01T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "나이", example = "38")
        int age,

        @Schema(description = "결혼 여부", example = "true")
        boolean maritalStatus,

        @Schema(description = "세대주 여부", example = "true")
        boolean isHouseholdHead,

        @Schema(description = "가구 소득 수준", example = "4구간")
        String incomeLevel,

        @Schema(description = "주택 소유 상태", example = "우리집 가구원 모두 주택을 소유하고 있지 않아요")
        String housingStatus

) {

    /**
     * Diagnosis 엔티티로부터 요약 정보 생성
     */
    public static DiagnosisHistoryResponse from(Diagnosis diagnosis) {
        return DiagnosisHistoryResponse.builder()
                .id(diagnosis.getId())
                .createdAt(diagnosis.getCreatedAt())
                .age(diagnosis.getAge())
                .maritalStatus(diagnosis.isMaritalStatus())
                .isHouseholdHead(diagnosis.isHouseholdHead())
                .incomeLevel(diagnosis.getIncomeLevel() != null ? diagnosis.getIncomeLevel().getValue() : "미입력")
                .housingStatus(diagnosis.getHousingStatus() != null ? diagnosis.getHousingStatus().getDescription() : "미입력")
                .build();
    }

    /**
     * 진단 목록을 요약 정보 목록으로 변환
     */
    public static List<DiagnosisHistoryResponse> fromList(List<Diagnosis> diagnoses) {
        return diagnoses.stream()
                .map(DiagnosisHistoryResponse::from)
                .toList();
    }
}
