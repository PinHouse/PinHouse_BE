package com.pinHouse.server.platform.housingFit.diagnosis.application.dto.request;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.*;
import com.pinHouse.server.platform.housingFit.rule.application.service.SubscriptionAccount;
import com.pinHouse.server.platform.user.domain.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DiagnosisRequest {

    /** 1) 기초 자격: 성별 */
    private Gender gender;                                           // 성별

    /** 2) 기초 자격: 나이 */
    private final int age;                                           // 나이

    /** 3-4) 지역 거주 요건 */
    private final String regionCode;                                 // 거주지역/해당지역 내 여부
    private final int localResidencyMonths;                          // 거주기간(월)

    /** 7-10) 청약통장 요건(가입기간/예치금/상품유형) */
    private final boolean hasAccount;
    private final SubscriptionPeriod accountYears;                    // 가입 년수(년)
    private final SubscriptionPaymentCount accountDeposit;            // 가입 횟수
    private final SubscriptionAccount account;                        // 가입 금액

    /** 11) 신혼 부부 요건 */
    private final boolean maritalStatus;                               // 결혼 여부
    private final Integer marriageYears;                               // 결혼 기간

    /** 12) 자녀(다자녀) 요건 */
    private final int unbornChildrenCount;                             // 태아 수
    private final int under6ChildrenCount;                             // 6세 이하 자녀 수
    private final int over7MinorChildrenCount;                         // 7세 이상 미성년 자녀 수
    private final int minorChildrenCount;                              // 미성년 자녀 총합

    /** 13) 특수 계층 필드 */
    private final HouseholdType householdType;                         // 특수 가구 특성

    /** 14) 대학생 요건*/
    private final EducationStatus educationStatus;                     // 학생 정보
    private final String schoolRegion;                                 // 대학 소재지
    private final boolean hasCar;                                      // 자동차 소유 여부
    private final long carValue;                                       // 자동차 가격

    /** 15) 특수 계층 요건 */
    private SpecialCategory hasSpecialCategory;                        // 특수 계층인지

    /** 16-17) 세대 관련 정보 */
    private final boolean isHouseholdHead;                             // 세대원, 세대주
    private final boolean isSingle;                                    // 1인 가구 여부
    private final int fetusCount;                                      // 태아 가구수
    private final int minorCount;                                      // 미성년자 가구수
    private final int adultCount;                                      // 성인 가구수
    private final int familyCount;                                     // 총 가구원수
    private final FamilySituation familySituation;

    /** 18) 세대 소득 요건 */
    private final IncomeLevel incomeLevel;                             // 가구 소득

    /** 19) 세대 주택 요건*/
    private final HousingOwnershipStatus housingStatus;                // 주택 소유 여부
    private final int housingYears;                                    // 무주택 기간 여부

    /** 20-22) 세대 자산 요건 */
    private final long propertyAsset;                                  // 부동산/토지 자산
    private final long carAsset;                                       // 자동차 가격
    private final long financialAsset;                                 // 금융자산

    /// 정적 팩토리 메서드 (도메인으로 변환)
    public Diagnosis toDomain() {
        return Diagnosis.builder()
                .gender(this.gender)
                .age(this.age)
                .region(this.regionCode)
                .localResidencyMonths(this.localResidencyMonths)

                .hasAccount(this.hasAccount)
                .accountYears(this.accountYears)
                .accountDeposit(this.accountDeposit)
                .account(this.account)

                .maritalStatus(this.maritalStatus)
                .marriageYears(this.marriageYears)

                .unbornChildrenCount(this.unbornChildrenCount)
                .under6ChildrenCount(this.under6ChildrenCount)
                .over7MinorChildrenCount(this.over7MinorChildrenCount)
                .minorChildrenCount(this.minorChildrenCount)

                .householdType(this.householdType)

                .educationStatus(this.educationStatus)
                .schoolRegion(this.schoolRegion)
                .hasCar(this.hasCar)
                .carValue(this.carValue)

                .hasSpecialCategory(this.hasSpecialCategory)

                .isHouseholdHead(this.isHouseholdHead)
                .isSingle(this.isSingle)
                .fetusCount(this.fetusCount)
                .minorCount(this.minorCount)
                .adultCount(this.adultCount)
                .familyCount(this.familyCount)
                .familySituation(this.familySituation)

                .incomeLevel(this.incomeLevel)

                .housingStatus(this.housingStatus)
                .housingYears(this.housingYears)

                .propertyAsset(this.propertyAsset)
                .carAsset(this.carAsset)
                .financialAsset(this.financialAsset)

                .build();

    }
}
