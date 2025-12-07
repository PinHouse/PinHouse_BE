package com.pinHouse.server.platform.diagnostic.diagnosis.application.dto;

import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.diagnostic.rule.application.dto.RuleResult;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.EvaluationContext;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 최종 진단 응답 DTO
 * - 어떤 Rule들을 통과/실패했는지
 * - 최종 적합 여부
 * - 요약 메시지
 * - 진단에 입력된 상세 정보
 */
@Schema(name = "[응답][진단] 청약 진단 응답", description = "진단 최종 결과를 응답하는 DTO입니다.")
@Builder
public record DiagnosisResponse(

        @Schema(description = "진단 기록 ID", example = "101")
        Long id,

        @Schema(description = "유저 ID", example = "1")
        UUID userId,

        @Schema(description = "진단 일시", example = "2025-12-01T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "최종 자격 여부", example = "true")
        boolean eligible,

        @Schema(description = "최종 요약 메시지 (예: \"국민임대주택 가능\", \"부적합\" 등)", example = "추천 임대주택이 있습니다")
        String decisionMessage,

        @Schema(description = "추천 후보 리스트", example = "[\"공공임대 : 장기\", \"민간임대 : 단기\"]")
        List<String> recommended,

        // === 진단 입력 정보 ===
        @Schema(description = "나이", example = "38")
        int age,

        @Schema(description = "성별", example = "남성")
        String gender,

        @Schema(description = "월 소득", example = "0")
        int monthPay,

        @Schema(description = "청약통장 보유 여부", example = "true")
        boolean hasAccount,

        @Schema(description = "청약통장 가입 기간", example = "2년 이상")
        String accountYears,

        @Schema(description = "결혼 여부", example = "true")
        boolean maritalStatus,

        @Schema(description = "결혼 기간(년)", example = "10")
        Integer marriageYears,

        @Schema(description = "태아 수", example = "1")
        int unbornChildrenCount,

        @Schema(description = "6세 이하 자녀 수", example = "1")
        int under6ChildrenCount,

        @Schema(description = "7세 이상 미성년 자녀 수", example = "1")
        int over7MinorChildrenCount,

        @Schema(description = "자동차 보유 여부", example = "false")
        boolean hasCar,

        @Schema(description = "자동차 가격", example = "0")
        long carValue,

        @Schema(description = "세대주 여부", example = "true")
        boolean isHouseholdHead,

        @Schema(description = "1인 가구 여부", example = "false")
        boolean isSingle,

        @Schema(description = "성인 가구 수", example = "2")
        int adultCount,

        @Schema(description = "미성년자 가구 수", example = "2")
        int minorCount,

        @Schema(description = "태아 가구 수", example = "1")
        int fetusCount,

        @Schema(description = "가구 소득 수준", example = "4구간")
        String incomeLevel,

        @Schema(description = "주택 소유 상태", example = "우리집 가구원 모두 주택을 소유하고 있지 않아요")
        String housingStatus,

        @Schema(description = "무주택 기간(년)", example = "0")
        int housingYears,

        @Schema(description = "부동산/토지 자산", example = "0")
        long propertyAsset,

        @Schema(description = "자동차 자산", example = "0")
        long carAsset,

        @Schema(description = "금융 자산", example = "0")
        long financialAsset,

        @Schema(description = "총 자산", example = "0")
        long totalAsset
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
                        .map((c -> c.noticeType().getValue() + " : " + c.supplyType().getValue()))
                        .toList();

        return DiagnosisResponse.builder()
                .id(diagnosis.getId())
                .userId(diagnosis.getUser().getId())
                .createdAt(diagnosis.getCreatedAt())
                .eligible(!recommended.contains("해당 없음"))
                .decisionMessage(!recommended.contains("해당 없음") ?
                        "추천 임대주택이 있습니다" : "모든 조건 미충족")
                .recommended(recommended)
                // 진단 입력 정보
                .age(diagnosis.getAge())
                .gender(diagnosis.getGender() != null ? diagnosis.getGender().getValue() : "미입력")
                .monthPay(diagnosis.getMonthPay())
                .hasAccount(diagnosis.isHasAccount())
                .accountYears(diagnosis.getAccountYears() != null ? diagnosis.getAccountYears().getDescription() : "미입력")
                .maritalStatus(diagnosis.isMaritalStatus())
                .marriageYears(diagnosis.getMarriageYears())
                .unbornChildrenCount(diagnosis.getUnbornChildrenCount())
                .under6ChildrenCount(diagnosis.getUnder6ChildrenCount())
                .over7MinorChildrenCount(diagnosis.getOver7MinorChildrenCount())
                .hasCar(diagnosis.isHasCar())
                .carValue(diagnosis.getCarValue())
                .isHouseholdHead(diagnosis.isHouseholdHead())
                .isSingle(diagnosis.isSingle())
                .adultCount(diagnosis.getAdultCount())
                .minorCount(diagnosis.getMinorCount())
                .fetusCount(diagnosis.getFetusCount())
                .incomeLevel(diagnosis.getIncomeLevel() != null ? diagnosis.getIncomeLevel().getValue() : "미입력")
                .housingStatus(diagnosis.getHousingStatus() != null ? diagnosis.getHousingStatus().getDescription() : "미입력")
                .housingYears(diagnosis.getHousingYears())
                .propertyAsset(diagnosis.getPropertyAsset())
                .carAsset(diagnosis.getCarAsset())
                .financialAsset(diagnosis.getFinancialAsset())
                .totalAsset(diagnosis.getTotalAsset())
                .build();
    }
}

