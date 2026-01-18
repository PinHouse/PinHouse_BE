package com.pinHouse.server.platform.diagnostic.diagnosis.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(name = "[응답][진단] 추천 그룹", description = "추천 임대 유형을 공고 유형별로 묶은 정보")
public record DiagnosisRecommendationGroup(

        @Schema(description = "공고 유형", example = "통합공공임대")
        String noticeType,

        @Schema(description = "추천 공급 유형 리스트", example = "[\"청년 특별공급\", \"신혼부부 특별공급\"]")
        List<String> supplyTypes
) {

    public static DiagnosisRecommendationGroup of(String noticeType, List<String> supplyTypes) {
        return DiagnosisRecommendationGroup.builder()
                .noticeType(noticeType)
                .supplyTypes(supplyTypes)
                .build();
    }
}
