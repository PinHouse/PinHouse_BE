package com.pinHouse.server.platform.housingFit.diagnosis.application.dto.response;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.SupplyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * 최종 진단 응답 DTO
 * - 어떤 Rule들을 통과/실패했는지
 * - 최종 적합 여부
 * - 요약 메시지
 */
@Data
@Builder
@AllArgsConstructor
public class DiagnosisResponse {

    private Long id;                    // 진단 기록 ID
    private UUID userId;                 // 유저 ID

    private boolean eligible;            // 최종 자격 여부
    private String decisionMessage;      // 최종 요약 메시지 ("국민임대주택 가능", "부적합" 등)
    private List<String> recommended;    // 추천 후보 리스트

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
                        .map((c -> c.supplyType().name() + " / " + c.rentalType().name()))
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

