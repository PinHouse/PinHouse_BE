package com.pinHouse.server.platform.diagnostic.diagnosis.application.dto;

import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.EvaluationContext;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 진단 결과 상세 조회 응답 DTO
 * - 진단에 입력된 정보
 * - 최종 진단 결과
 */
@Schema(name = "[응답][진단] 청약 진단 상세 결과", description = "청약 진단 입력 정보와 최종 결과를 함께 응답하는 DTO")
@Builder
public record DiagnosisDetailResponse(

        // === 진단 메타 정보 ===
        @Schema(description = "진단 ID", example = "101")
        Long diagnosisId,

        @Schema(description = "진단 일시", example = "2025-12-28T10:30:00")
        LocalDateTime diagnosedAt,

        @Schema(description = "사용자 닉네임", example = "집구하는청년")
        String nickname,

        // === 1. 성별/나이 ===
        @Schema(description = "성별", example = "여자")
        String gender,

        @Schema(description = "나이 (만 나이)", example = "28")
        int age,

        // === 최종 진단 결과 ===
        @Schema(description = "내 소득분위", example = "소득 1분위")
        String myIncomeLevel,

        @Schema(description = "나의 지원 가능 대상 (공급 유형)", example = "[\"청년 특별공급\", \"일반 공급\"]")
        List<String> availableSupplyTypes,

        @Schema(description = "신청가능한 임대주택 (주택 유형)", example = "[\"공공임대\", \"민간임대\"]")
        List<String> availableRentalTypes,

        @Schema(description = "최종 자격 여부", example = "true")
        boolean eligible,

        @Schema(description = "진단 결과 메시지", example = "추천 임대주택이 있습니다")
        String diagnosisResult,

        @Schema(description = "추천 임대주택 후보 리스트", example = "[\"공공임대 : 장기\", \"민간임대 : 단기\"]")
        List<String> recommended
) {

    /// 정적 팩토리 메서드
    public static DiagnosisDetailResponse from(EvaluationContext context) {

        Diagnosis diagnosis = context.getDiagnosis();

        // 추천 후보 계산
        List<String> recommended = context.getCurrentCandidates().isEmpty() ?
                List.of("해당 없음") :
                context.getCurrentCandidates().stream()
                        .map((c -> c.noticeType().getValue() + " : " + c.supplyType().getValue()))
                        .toList();

        // 지원 가능한 공급 유형 (중복 제거)
        List<String> availableSupplyTypes = context.getCurrentCandidates().isEmpty() ?
                List.of("해당 없음") :
                context.getCurrentCandidates().stream()
                        .map(c -> c.supplyType().getValue())
                        .distinct()
                        .toList();

        // 신청 가능한 임대주택 유형 (중복 제거)
        List<String> availableRentalTypes = context.getCurrentCandidates().isEmpty() ?
                List.of("해당 없음") :
                context.getCurrentCandidates().stream()
                        .map(c -> c.noticeType().getValue())
                        .distinct()
                        .toList();

        return DiagnosisDetailResponse.builder()
                // 메타 정보
                .diagnosisId(diagnosis.getId())
                .diagnosedAt(diagnosis.getCreatedAt())
                .nickname(diagnosis.getUser() != null ? diagnosis.getUser().getNickname() : "알 수 없음")
                // 1. 성별/나이
                .gender(diagnosis.getGender() != null ? diagnosis.getGender().getValue() : "미입력")
                .age(diagnosis.getAge())
                // 최종 진단 결과
                .myIncomeLevel(diagnosis.getIncomeLevel() != null ? diagnosis.getIncomeLevel().getValue() : "미입력")
                .availableSupplyTypes(availableSupplyTypes)
                .availableRentalTypes(availableRentalTypes)
                .eligible(!recommended.contains("해당 없음"))
                .diagnosisResult(!recommended.contains("해당 없음") ?
                        "추천 임대주택이 있습니다" : "모든 조건 미충족")
                .recommended(recommended)
                .build();
    }
}
