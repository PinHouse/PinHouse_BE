package com.pinHouse.server.platform.diagnostic.diagnosis.application.dto;

import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.*;
import com.pinHouse.server.platform.user.domain.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Schema(name = "[요청][진단] 청약 진단 요청", description = "진단 요청에 필요한 정보를 담는 DTO입니다.")
@Builder
public record DiagnosisRequest(

        @Schema(description = "성별", example = "남성")
        Gender gender,                                           // 성별

        @Schema(description = "생일", example = "2000-06-15")
        LocalDate birthday,                                      // 생일

        @Schema(description = "월 소득", example = "2000000")
        int monthPay,                                            // 소득 여부

        @Schema(description = "청약통장 보유 여부", example = "true")
        boolean hasAccount,

        @Schema(description = "청약통장 가입 기간", example = "6개월 이상 ~ 1년 미만")
        SubscriptionPeriod accountYears,                         // 가입 년수(년)

        @Schema(description = "청약통장 납입 횟수", example = "6회 ~ 11회")
        SubscriptionCount accountDeposit,                        // 가입 횟수

        @Schema(description = "청약통장 금액", example = "600만원 이상")
        SubscriptionAccount account,                              // 가입 금액

        @Schema(description = "결혼 여부", example = "false")
        boolean maritalStatus,                                   // 결혼 여부

        @Schema(description = "결혼 기간(년)", example = "0")
        Integer marriageYears,                                   // 결혼 기간

        @Schema(description = "태아 수", example = "0")
        int unbornChildrenCount,                                 // 태아 수

        @Schema(description = "6세 이하 자녀 수", example = "0")
        int under6ChildrenCount,                                 // 6세 이하 자녀 수

        @Schema(description = "7세 이상 미성년 자녀 수", example = "0")
        int over7MinorChildrenCount,                             // 7세 이상 미성년 자녀 수

        @Schema(description = "교육 상태", example = "대학교 휴학 중이며 다음 학기 복학 예정")
        EducationStatus educationStatus,                         // 학생 정보

        @Schema(description = "자동차 보유 여부", example = "false")
        boolean hasCar,                                          // 자동차 소유 여부

        @Schema(description = "자동차 가격", example = "0")
        long carValue,                                           // 자동차 가격

        @Schema(description = "세대주 여부", example = "true")
        boolean isHouseholdHead,                                 // 세대원, 세대주

        @Schema(description = "1인 가구 여부", example = "true")
        boolean isSingle,                                        // 1인 가구 여부 - false면 가족들과 거주

        @Schema(description = "태아 가구 수", example = "0")
        int fetusCount,                                          // 태아 가구수

        @Schema(description = "미성년자 가구 수", example = "0")
        int minorCount,                                          // 미성년자 가구수

        @Schema(description = "성인 가구 수", example = "1")
        int adultCount,                                          // 성인 가구수

        @Schema(description = "가구 소득 수준", example = "2구간")
        IncomeLevel incomeLevel,                                 // 가구 소득

        @Schema(description = "주택 소유 상태", example = "우리집 가구원 모두 주택을 소유하고 있지 않아요")
        HousingOwnershipStatus housingStatus,                    // 주택 소유 여부

        @Schema(description = "무주택 기간(년)", example = "3")
        int housingYears,                                        // 무주택 기간 여부

        @Schema(description = "부동산/토지 자산", example = "0")
        long propertyAsset,                                      // 부동산/토지 자산

        @Schema(description = "자동차 자산", example = "0")
        long carAsset,                                           // 자동차 가격

        @Schema(description = "금융 자산", example = "1000000")
        long financialAsset,                                     // 금융자산

        @Schema(description = "특수 계층 목록", example = "[\"주거급여 수급자\"]")
        List<SpecialCategory> hasSpecialCategory                // 특수 계층인지

) {

}
