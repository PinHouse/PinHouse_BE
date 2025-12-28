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

        // === 1. 성별/나이 ===
        @Schema(description = "성별", example = "여자")
        String gender,

        @Schema(description = "나이 (만 나이)", example = "28")
        int age,

        // === 2. 소득 ===
        @Schema(description = "가구 소득 수준", example = "소득 1분위")
        String incomeLevel,

        @Schema(description = "월 소득 (원)", example = "2000000")
        int monthPay,

        // === 3. 대학생 여부 ===
        @Schema(description = "대학생 여부", example = "false")
        boolean isCollegeStudent,

        // === 4. 청약 저축 ===
        @Schema(description = "청약통장 보유 여부", example = "true")
        boolean hasSubscription,

        @Schema(description = "청약통장 가입 기간", example = "2년 이상")
        String subscriptionYears,

        @Schema(description = "청약통장 납입 횟수", example = "24회 이상")
        String subscriptionCount,

        @Schema(description = "청약통장 예치금", example = "600만원 이하")
        String subscriptionAmount,

        // === 5. 결혼 여부 ===
        @Schema(description = "결혼 여부", example = "true")
        boolean isMarried,

        @Schema(description = "결혼 기간 (년)", example = "5")
        Integer marriedYears,

        // === 6. 자녀 여부 ===
        @Schema(description = "태아 수", example = "0")
        int unbornChildren,

        @Schema(description = "6세 이하 자녀 수", example = "1")
        int under6Children,

        @Schema(description = "7세 이상 미성년 자녀 수", example = "1")
        int over7MinorChildren,

        @Schema(description = "총 자녀 수", example = "2")
        int totalChildren,

        // === 7. 세대 정보 ===
        @Schema(description = "세대주 여부", example = "true")
        boolean isHouseholdHead,

        @Schema(description = "1인 가구 여부", example = "false")
        boolean isSingleHousehold,

        @Schema(description = "전체 세대원 수", example = "4")
        int householdSize,

        // === 8. 주택 소유 여부 ===
        @Schema(description = "주택 소유 상태", example = "우리집 가구원 모두 주택을 소유하고 있지 않아요")
        String housingStatus,

        @Schema(description = "무주택 기간 (년)", example = "5")
        int housingYears,

        // === 9. 자동차 소유 여부 ===
        @Schema(description = "자동차 보유 여부", example = "false")
        boolean ownsCar,

        @Schema(description = "자동차 가격 (원)", example = "0")
        long carValue,

        // === 10. 총 자산 ===
        @Schema(description = "부동산/토지 자산 (원)", example = "0")
        long propertyAsset,

        @Schema(description = "자동차 자산 (원)", example = "0")
        long carAsset,

        @Schema(description = "금융 자산 (원)", example = "50000000")
        long financialAsset,

        @Schema(description = "총 자산 (원)", example = "50000000")
        long totalAssets,

        // === 최종 진단 결과 ===
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

        // 총 자녀 수 계산
        int totalChildren = diagnosis.getUnbornChildrenCount()
                + diagnosis.getUnder6ChildrenCount()
                + diagnosis.getOver7MinorChildrenCount();

        return DiagnosisDetailResponse.builder()
                // 메타 정보
                .diagnosisId(diagnosis.getId())
                .diagnosedAt(diagnosis.getCreatedAt())
                // 1. 성별/나이
                .gender(diagnosis.getGender() != null ? diagnosis.getGender().getValue() : "미입력")
                .age(diagnosis.getAge())
                // 2. 소득
                .incomeLevel(diagnosis.getIncomeLevel() != null ? diagnosis.getIncomeLevel().getValue() : "미입력")
                .monthPay(diagnosis.getMonthPay())
                // 3. 대학생 여부
                .isCollegeStudent(diagnosis.getEducationStatus() != null)
                // 4. 청약 저축
                .hasSubscription(diagnosis.isHasAccount())
                .subscriptionYears(diagnosis.getAccountYears() != null ? diagnosis.getAccountYears().getDescription() : "미입력")
                .subscriptionCount(diagnosis.getAccountDeposit() != null ? diagnosis.getAccountDeposit().getDescription() : "미입력")
                .subscriptionAmount(diagnosis.getAccount() != null ? diagnosis.getAccount().getValue() : "미입력")
                // 5. 결혼 여부
                .isMarried(diagnosis.isMaritalStatus())
                .marriedYears(diagnosis.getMarriageYears())
                // 6. 자녀 여부
                .unbornChildren(diagnosis.getUnbornChildrenCount())
                .under6Children(diagnosis.getUnder6ChildrenCount())
                .over7MinorChildren(diagnosis.getOver7MinorChildrenCount())
                .totalChildren(totalChildren)
                // 7. 세대 정보
                .isHouseholdHead(diagnosis.isHouseholdHead())
                .isSingleHousehold(diagnosis.isSingle())
                .householdSize(diagnosis.getFamilyCount())
                // 8. 주택 소유 여부
                .housingStatus(diagnosis.getHousingStatus() != null ? diagnosis.getHousingStatus().getDescription() : "미입력")
                .housingYears(diagnosis.getHousingYears())
                // 9. 자동차 소유 여부
                .ownsCar(diagnosis.isHasCar())
                .carValue(diagnosis.getCarValue())
                // 10. 총 자산
                .propertyAsset(diagnosis.getPropertyAsset())
                .carAsset(diagnosis.getCarAsset())
                .financialAsset(diagnosis.getFinancialAsset())
                .totalAssets(diagnosis.getTotalAsset())
                // 최종 진단 결과
                .eligible(!recommended.contains("해당 없음"))
                .diagnosisResult(!recommended.contains("해당 없음") ?
                        "추천 임대주택이 있습니다" : "모든 조건 미충족")
                .recommended(recommended)
                .build();
    }
}
