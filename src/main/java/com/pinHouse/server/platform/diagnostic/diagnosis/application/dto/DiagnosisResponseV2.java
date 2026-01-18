package com.pinHouse.server.platform.diagnostic.diagnosis.application.dto;

import com.pinHouse.server.platform.diagnostic.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.SupplyRentalCandidate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Builder
@Schema(name = "[응답][진단] 청약 진단 결과 v2", description = "추천 임대주택을 공고 유형별로 그룹화한 응답")
public record DiagnosisResponseV2(

        @Schema(description = "최종 자격 여부 (추천 임대주택이 있는지 여부)", example = "true")
        boolean eligible,

        @Schema(description = "최종 요약 메시지", example = "추천 임대주택이 있습니다")
        String decisionMessage,

        @Schema(description = "추천 임대주택 후보 그룹", example = "[{\"noticeType\":\"통합공공임대\",\"supplyTypes\":[\"청년 특별공급\",\"신혼부부 특별공급\"]}]")
        List<DiagnosisRecommendationGroup> recommended
) {

    public static DiagnosisResponseV2 from(EvaluationContext context) {
        List<SupplyRentalCandidate> candidates = context.getCurrentCandidates();

        if (candidates.isEmpty()) {
            return DiagnosisResponseV2.builder()
                    .eligible(false)
                    .decisionMessage("모든 조건 미충족")
                    .recommended(List.of())
                    .build();
        }

        Map<String, List<String>> grouped = new LinkedHashMap<>();

        for (SupplyRentalCandidate candidate : candidates) {
            String noticeType = candidate.noticeType().getValue();
            String supplyType = candidate.supplyType().getValue();
            grouped.computeIfAbsent(noticeType, k -> new ArrayList<>());
            if (!grouped.get(noticeType).contains(supplyType)) {
                grouped.get(noticeType).add(supplyType);
            }
        }

        List<DiagnosisRecommendationGroup> groups = grouped.entrySet().stream()
                .map(e -> DiagnosisRecommendationGroup.of(e.getKey(), e.getValue()))
                .toList();

        return DiagnosisResponseV2.builder()
                .eligible(true)
                .decisionMessage("추천 임대주택이 있습니다")
                .recommended(groups)
                .build();
    }
}
