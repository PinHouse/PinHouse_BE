package com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.request;

import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.*;
import com.pinHouse.server.platform.user.domain.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "진단 요청 DTO")
public class DiagnosisRequest {

    /** 1) 기초 자격: 성별 */
    @Schema(description = "성별", example = "남성")
    private Gender gender;                                           // 성별

    /** 2) 기초 자격: 나이 */
    @Schema(description = "생일", example = "2000-06-15")
    private LocalDate birthday;                                      // 생일

    /** 5-6) 소득 요건(일자리 종사) */
    @Schema(description = "월 소득", example = "2000000")
    private int monthPay;                                            // 소득 여부

    /** 7-10) 청약통장 요건(가입기간/예치금/상품유형) */
    @Schema(description = "청약통장 보유 여부", example = "true")
    private boolean hasAccount;

    @Schema(description = "청약통장 가입 기간", example = "6개월 이상 ~ 1년 미만")
    private SubscriptionPeriod accountYears;                         // 가입 년수(년)

    @Schema(description = "청약통장 납입 횟수", example = "6회 ~ 11회")
    private SubscriptionCount accountDeposit;                        // 가입 횟수

    @Schema(description = "청약통장 금액", example = "600만원 이상")
    private SubscriptionAccount account;                              // 가입 금액

    /** 11) 신혼 부부 요건 */
    @Schema(description = "결혼 여부", example = "false")
    private boolean maritalStatus;                                   // 결혼 여부

    @Schema(description = "결혼 기간(년)", example = "0")
    private Integer marriageYears;                                   // 결혼 기간

    /** 12) 자녀(다자녀) 요건 */
    @Schema(description = "태아 수", example = "0")
    private int unbornChildrenCount;                                 // 태아 수

    @Schema(description = "6세 이하 자녀 수", example = "0")
    private int under6ChildrenCount;                                 // 6세 이하 자녀 수

    @Schema(description = "7세 이상 미성년 자녀 수", example = "0")
    private int over7MinorChildrenCount;                             // 7세 이상 미성년 자녀 수

    /** 14) 대학생 요건 */
    @Schema(description = "교육 상태", example = "대학교 휴학 중이며 다음 학기 복학 예정")
    private EducationStatus educationStatus;                         // 학생 정보

    @Schema(description = "자동차 보유 여부", example = "false")
    private boolean hasCar;                                          // 자동차 소유 여부

    @Schema(description = "자동차 가격", example = "0")
    private long carValue;                                           // 자동차 가격

    /** 16-17) 세대 관련 정보 */
    @Schema(description = "세대주 여부", example = "true")
    private boolean isHouseholdHead;                                 // 세대원, 세대주

    @Schema(description = "1인 가구 여부", example = "true")
    private boolean isSingle;                                        // 1인 가구 여부 - false면 가족들과 거주

    @Schema(description = "태아 가구 수", example = "0")
    private int fetusCount;                                          // 태아 가구수

    @Schema(description = "미성년자 가구 수", example = "0")
    private int minorCount;                                          // 미성년자 가구수

    @Schema(description = "성인 가구 수", example = "1")
    private int adultCount;                                          // 성인 가구수

    /** 18) 세대 소득 요건 */
    @Schema(description = "가구 소득 수준", example = "2구간")
    private IncomeLevel incomeLevel;                                 // 가구 소득

    /** 19) 세대 주택 요건 */
    @Schema(description = "주택 소유 상태", example = "우리집 가구원 모두 주택을 소유하고 있지 않아요")
    private HousingOwnershipStatus housingStatus;                    // 주택 소유 여부

    @Schema(description = "무주택 기간(년)", example = "3")
    private int housingYears;                                        // 무주택 기간 여부

    /** 20-22) 세대 자산 요건 */
    @Schema(description = "부동산/토지 자산", example = "0")
    private long propertyAsset;                                      // 부동산/토지 자산

    @Schema(description = "자동차 자산", example = "0")
    private long carAsset;                                           // 자동차 가격

    @Schema(description = "금융 자산", example = "1000000")
    private long financialAsset;                                     // 금융자산

    /** 최종) 특수 계층 요건 */
    @Schema(description = "특수 계층 목록", example = "[\"주거급여 수급자\"]")
    private List<SpecialCategory> hasSpecialCategory;                // 특수 계층인지

}
