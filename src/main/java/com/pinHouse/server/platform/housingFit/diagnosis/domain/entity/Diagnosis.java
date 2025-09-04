package com.pinHouse.server.platform.housingFit.diagnosis.domain.entity;

import com.pinHouse.server.platform.BaseTimeEntity;
import com.pinHouse.server.platform.housingFit.diagnosis.application.dto.request.DiagnosisRequest;
import com.pinHouse.server.platform.user.domain.entity.Gender;
import com.pinHouse.server.platform.user.domain.entity.User;
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
public class Diagnosis extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /** 1) 기초 자격: 성별 */
    @Enumerated(EnumType.STRING)
    private Gender gender;                                           // 성별

    /** 2) 기초 자격: 나이 */
    private int age;                                           // 나이

    /**
     * 3-4) 지역 거주 요건
     */
    private String region;                                     // 거주지역/해당지역 내 여부
    private int localResidencyMonths;                          // 거주기간(월)

    /**
     * 7-10) 청약통장 요건(가입기간/예치금/상품유형)
     */
    private boolean hasAccount;
    private SubscriptionPeriod accountYears;                    // 가입 년수(년)
    private SubscriptionPaymentCount accountDeposit;            // 가입 횟수
    private SubscriptionAccount account;                        // 가입 금액

    /**
     * 11) 신혼 부부 요건
     */
    private boolean maritalStatus;                               // 결혼 여부
    private Integer marriageYears;                               // 결혼 기간

    /**
     * 12) 자녀(다자녀) 요건
     */
    private int unbornChildrenCount;                             // 태아 수
    private int under6ChildrenCount;                             // 6세 이하 자녀 수
    private  int over7MinorChildrenCount;                         // 7세 이상 미성년 자녀 수


    private int minorChildrenCount;                              // 미성년 자녀 총합

    /** 13) 특수 계층 필드 */
    private HouseholdType householdType;                         // 특수 가구 특성

    /** 14) 대학생 요건*/
    private EducationStatus educationStatus;                     // 학생 정보
    private  String schoolRegion;                                 // 대학 소재지
    private  boolean hasCar;                                      // 자동차 소유 여부
    private  long carValue;                                       // 자동차 가격

    /** 15) 특수 계층 요건 */
    private SpecialCategory hasSpecialCategory;                        // 특수 계층인지

    /**
     * 16-17) 세대 관련 정보
     */
    private boolean isHouseholdHead;                             // 세대원, 세대주
    private boolean isSingle;                                    // 1인 가구 여부
    private boolean hasHousehold;                                // 주택 소유 여부
    private int fetusCount;                                      // 태아 가구수
    private int minorCount;                                      // 미성년자 가구수
    private int adultCount;                                      // 성인 가구수
    private int familyCount;                                     // 총 가구원수
    private FamilySituation familySituation;

    /** 18) 세대 소득 요건 */
    private IncomeLevel incomeLevel;                             // 가구 소득

    /**
     * 19) 세대 주택 요건
     */
    private HousingOwnershipStatus housingStatus;                // 주택 소유 여부
    private int housingYears;                                    // 무주택 기간 여부

    /**
     * 20-22) 세대 자산 요건
     */
    private long propertyAsset;                                  // 부동산/토지 자산
    private long carAsset;                                       // 자동차 가격
    private long financialAsset;                                 // 금융자산


    /// 정적 팩토리 메서드
    public static Diagnosis of(
            User user,
            Gender gender,
            int age,
            String region,
            int localResidencyMonths,
            boolean hasAccount,
            SubscriptionPeriod accountYears,
            SubscriptionPaymentCount accountDeposit,
            SubscriptionAccount account,
            boolean maritalStatus,
            Integer marriageYears,
            int unbornChildrenCount,
            int under6ChildrenCount,
            int over7MinorChildrenCount,
            int minorChildrenCount,
            HouseholdType householdType,
            EducationStatus educationStatus,
            String schoolRegion,
            boolean hasCar,
            long carValue,
            SpecialCategory hasSpecialCategory,
            boolean isHouseholdHead,
            boolean isSingle,
            boolean hasHousehold,
            int fetusCount,
            int minorCount,
            int adultCount,
            int familyCount,
            FamilySituation familySituation,
            IncomeLevel incomeLevel,
            HousingOwnershipStatus housingStatus,
            int housingYears,
            long propertyAsset,
            long carAsset,
            long financialAsset
    ) {
        return Diagnosis.builder()
                .user(user)
                .gender(gender)
                .age(age)
                .region(region)
                .localResidencyMonths(localResidencyMonths)
                .hasAccount(hasAccount)
                .accountYears(accountYears)
                .accountDeposit(accountDeposit)
                .account(account)
                .maritalStatus(maritalStatus)
                .marriageYears(marriageYears)
                .unbornChildrenCount(unbornChildrenCount)
                .under6ChildrenCount(under6ChildrenCount)
                .over7MinorChildrenCount(over7MinorChildrenCount)
                .minorChildrenCount(minorChildrenCount)
                .householdType(householdType)
                .educationStatus(educationStatus)
                .schoolRegion(schoolRegion)
                .hasCar(hasCar)
                .carValue(carValue)
                .hasSpecialCategory(hasSpecialCategory)
                .isHouseholdHead(isHouseholdHead)
                .isSingle(isSingle)
                .hasHousehold(hasHousehold)
                .fetusCount(fetusCount)
                .minorCount(minorCount)
                .adultCount(adultCount)
                .familyCount(familyCount)
                .familySituation(familySituation)
                .incomeLevel(incomeLevel)
                .housingStatus(housingStatus)
                .housingYears(housingYears)
                .propertyAsset(propertyAsset)
                .carAsset(carAsset)
                .financialAsset(financialAsset)
                .build();
    }

    /// 정적 팩토리 메서드
    public static Diagnosis of(User user, DiagnosisRequest request) {

        return Diagnosis.builder()
                .user(user)
                .gender(request.getGender())
                .age(request.getAge())
                .region(request.getRegionCode())
                .localResidencyMonths(request.getLocalResidencyMonths())
                .hasAccount(request.isHasAccount())
                .accountYears(request.getAccountYears())
                .accountDeposit(request.getAccountDeposit())
                .account(request.getAccount())
                .maritalStatus(request.isMaritalStatus())
                .marriageYears(request.getMarriageYears())
                .unbornChildrenCount(request.getUnbornChildrenCount())
                .under6ChildrenCount(request.getUnder6ChildrenCount())
                .over7MinorChildrenCount(request.getOver7MinorChildrenCount())
                .minorChildrenCount(request.getMinorChildrenCount())
                .householdType(request.getHouseholdType())
                .educationStatus(request.getEducationStatus())
                .schoolRegion(request.getSchoolRegion())
                .hasCar(request.isHasCar())
                .carValue(request.getCarValue())
                .hasSpecialCategory(request.getHasSpecialCategory())
                .isHouseholdHead(request.isHouseholdHead())
                .isSingle(request.isSingle())
                .hasHousehold(request.isHasHousehold())
                .fetusCount(request.getFetusCount())
                .minorCount(request.getMinorCount())
                .adultCount(request.getAdultCount())
                .familyCount(request.getFamilyCount())
                .familySituation(request.getFamilySituation())
                .incomeLevel(request.getIncomeLevel())
                .housingStatus(request.getHousingStatus())
                .housingYears(request.getHousingYears())
                .propertyAsset(request.getPropertyAsset())
                .carAsset(request.getCarAsset())
                .financialAsset(request.getFinancialAsset())
                .build();
    }

}
