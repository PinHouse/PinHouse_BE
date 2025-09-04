package com.pinHouse.server.platform.adapter.out.jpa.diagnosis;

import com.pinHouse.server.platform.adapter.out.jpa.BaseTimeEntity;
import com.pinHouse.server.platform.application.service.SubscriptionAccount;
import com.pinHouse.server.platform.domain.diagnosis.entity.*;
import com.pinHouse.server.platform.domain.user.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class DiagnosisJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long user;

    /** 1) 기초 자격: 성별 */
    private Gender gender;                                           // 성별

    /** 2) 기초 자격: 나이 */
    private int age;                                           // 나이

    /** 3-4) 지역 거주 요건 */
    private  String region;                                     // 거주지역/해당지역 내 여부
    private  int localResidencyMonths;                          // 거주기간(월)

    /** 7-10) 청약통장 요건(가입기간/예치금/상품유형) */
    private  boolean hasAccount;
    private  SubscriptionPeriod accountYears;                    // 가입 년수(년)
    private  SubscriptionPaymentCount accountDeposit;            // 가입 횟수
    private  SubscriptionAccount account;                        // 가입 금액

    /** 11) 신혼 부부 요건 */
    private  boolean maritalStatus;                               // 결혼 여부
    private  Integer marriageYears;                               // 결혼 기간

    /** 12) 자녀(다자녀) 요건 */
    private  int unbornChildrenCount;                             // 태아 수
    private  int under6ChildrenCount;                             // 6세 이하 자녀 수
    private  int over7MinorChildrenCount;                         // 7세 이상 미성년 자녀 수
    private  int minorChildrenCount;                              // 미성년 자녀 총합

    /** 13) 특수 계층 필드 */
    private  HouseholdType householdType;                         // 특수 가구 특성

    /** 14) 대학생 요건*/
    private  EducationStatus educationStatus;                     // 학생 정보
    private  String schoolRegion;                                 // 대학 소재지
    private  boolean hasCar;                                      // 자동차 소유 여부
    private  long carValue;                                       // 자동차 가격

    /** 15) 특수 계층 요건 */
    private SpecialCategory hasSpecialCategory;                        // 특수 계층인지

    /** 16-17) 세대 관련 정보 */
    private  boolean isHouseholdHead;                             // 세대원, 세대주
    private  boolean isSingle;                                    // 1인 가구 여부
    private  boolean hasHousehold;                                // 주택 소유 여부
    private  int fetusCount;                                      // 태아 가구수
    private  int minorCount;                                      // 미성년자 가구수
    private  int adultCount;                                      // 성인 가구수
    private  int familyCount;                                     // 총 가구원수
    private  FamilySituation familySituation;

    /** 18) 세대 소득 요건 */
    private  IncomeLevel incomeLevel;                             // 가구 소득

    /** 19) 세대 주택 요건*/
    private  HousingOwnershipStatus housingStatus;                // 주택 소유 여부
    private  int housingYears;                                    // 무주택 기간 여부

    /** 20-22) 세대 자산 요건 */
    private  long propertyAsset;                                  // 부동산/토지 자산
    private  long carAsset;                                       // 자동차 가격
    private  long financialAsset;                                 // 금융자산

    /// 정적 팩토리 메서드
    public static DiagnosisJpaEntity from(Diagnosis diagnosis) {
        return DiagnosisJpaEntity.builder()
                .id(diagnosis.getId())
                .user(diagnosis.getUserId())
                .gender(diagnosis.getGender())
                .age(diagnosis.getAge())
                .region(diagnosis.getRegion())
                .localResidencyMonths(diagnosis.getLocalResidencyMonths())
                .hasAccount(diagnosis.isHasAccount())
                .accountYears(diagnosis.getAccountYears())
                .accountDeposit(diagnosis.getAccountDeposit())
                .account(diagnosis.getAccount())
                .maritalStatus(diagnosis.isMaritalStatus())
                .marriageYears(diagnosis.getMarriageYears())
                .unbornChildrenCount(diagnosis.getUnbornChildrenCount())
                .under6ChildrenCount(diagnosis.getUnder6ChildrenCount())
                .over7MinorChildrenCount(diagnosis.getOver7MinorChildrenCount())
                .minorChildrenCount(diagnosis.getMinorChildrenCount())
                .householdType(diagnosis.getHouseholdType())
                .educationStatus(diagnosis.getEducationStatus())
                .schoolRegion(diagnosis.getSchoolRegion())
                .hasCar(diagnosis.isHasCar())
                .carValue(diagnosis.getCarValue())
                .hasSpecialCategory(diagnosis.getHasSpecialCategory())
                .isHouseholdHead(diagnosis.isHouseholdHead())
                .isSingle(diagnosis.isSingle())
                .hasHousehold(diagnosis.isHasHousehold())
                .fetusCount(diagnosis.getFetusCount())
                .minorCount(diagnosis.getMinorCount())
                .adultCount(diagnosis.getAdultCount())
                .familyCount(diagnosis.getFamilyCount())
                .familySituation(diagnosis.getFamilySituation())
                .incomeLevel(diagnosis.getIncomeLevel())
                .housingStatus(diagnosis.getHousingStatus())
                .housingYears(diagnosis.getHousingYears())
                .propertyAsset(diagnosis.getPropertyAsset())
                .carAsset(diagnosis.getCarAsset())
                .financialAsset(diagnosis.getFinancialAsset())
                .build();
    }

    /// toDomain
    public Diagnosis toDomain() {
        return Diagnosis.builder()
                .id(this.id)
                .userId(this.user)
                .gender(this.gender)
                .age(this.age)
                .region(this.region)
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
                .hasHousehold(this.hasHousehold)
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
