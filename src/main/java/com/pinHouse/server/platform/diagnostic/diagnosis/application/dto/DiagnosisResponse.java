package com.pinHouse.server.platform.diagnostic.diagnosis.application.dto;

import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.diagnostic.rule.application.dto.RuleResult;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.EvaluationContext;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

/**
 * 최종 진단 응답 DTO
 * - 어떤 Rule들을 통과/실패했는지
 * - 최종 적합 여부
 * - 요약 메시지
 */
@Schema(name = "[응답][진단] 청약 진단 응답", description = "진단 최종 결과를 응답하는 DTO입니다.")
@Builder
public record DiagnosisResponse(

        @Schema(description = "진단 기록 ID", example = "101")
        Long id,

        @Schema(description = "유저 ID", example = "1")
        UUID userId,

        @Schema(description = "최종 자격 여부", example = "true")
        boolean eligible,

        @Schema(description = "최종 요약 메시지 (예: \"국민임대주택 가능\", \"부적합\" 등)", example = "추천 임대주택이 있습니다")
        String decisionMessage,

        @Schema(description = "추천 후보 리스트", example = "[\"공공임대 : 장기\", \"민간임대 : 단기\"]")
        List<String> recommended
) {


    /// 정적 팩토리 메서드
    public static DiagnosisResponse from(EvaluationContext context) {

        Diagnosis diagnosis = context.getDiagnosis();

        // 실패 이유만 추출
        List<String> failureReasons = context.getRuleResults().stream()
                .filter(r -> !r.pass())
                .map(RuleResult::message)
                .toList();

        // 추천 후보 (후보군이 남아있으면 추천, 없으면 "해당 없음")
        List<String> recommended = context.getCurrentCandidates().isEmpty() ?
                List.of("해당 없음") :
                context.getCurrentCandidates().stream()
                        .map((c -> c.rentalType().getValue() + " : " + c.supplyType().getValue()))
                        .toList();

        return DiagnosisResponse.builder()
                .id(diagnosis.getId())
                .userId(diagnosis.getUser().getId())
                .eligible(!recommended.contains("해당 없음"))
                .decisionMessage(!recommended.contains("해당 없음") ?
                        "추천 임대주택이 있습니다" : "모든 조건 미충족")
                .recommended(recommended)
                .build();
    }
}

