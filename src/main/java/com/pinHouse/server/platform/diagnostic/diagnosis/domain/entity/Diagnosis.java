package com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity;

import com.pinHouse.server.core.util.BirthDayUtil;
import com.pinHouse.server.platform.BaseTimeEntity;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisRequest;
import com.pinHouse.server.platform.user.domain.entity.Gender;
import com.pinHouse.server.platform.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
     * 5-6) 소득 요건(일자리 종사)
     */
    private int monthPay;                                       // 소득 여부

    /**
     * 7-10) 청약통장 요건(가입기간/예치금/상품유형)
     */
    private boolean hasAccount;
    private SubscriptionPeriod accountYears;                    // 가입 년수(년)
    private SubscriptionCount accountDeposit;                   // 가입 횟수
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
    private int over7MinorChildrenCount;                         // 7세 이상 미성년 자녀 수

    /**
     * 14) 대학생 요건
     */
    private EducationStatus educationStatus;                     // 학생 정보
    private boolean hasCar;                                      // 자동차 소유 여부
    private long carValue;                                       // 자동차 가격

    /**
     * 16-17) 세대 관련 정보
     */
    private boolean isHouseholdHead;                             // 세대원, 세대주
    private boolean isSingle;                                    // 1인 가구 여부 - false면 가족들과 거주
    private int fetusCount;                                      // 태아 가구수
    private int minorCount;                                      // 미성년자 가구수
    private int adultCount;                                      // 성인 가구수

    /**
     * 18) 세대 소득 요건
     */
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
    private long totalAsset;                                     // 총자산

    /**
     * 최종) 특수 계층 요건
     */
    @ElementCollection(targetClass = SpecialCategory.class)
    @Enumerated(EnumType.STRING)
    private List<SpecialCategory> hasSpecialCategory = new ArrayList<>();                        // 특수 계층인지

    /// 정적 팩토리 메서드
    public static Diagnosis of(User user, DiagnosisRequest request) {
        return Diagnosis.builder()
                .user(user)
                .gender(request.gender())
                .age(BirthDayUtil.calculateAge(request.birthday()))
                .monthPay(request.monthPay())
                .hasAccount(request.hasAccount())
                .accountYears(request.accountYears())
                .accountDeposit(request.accountDeposit())
                .account(request.account())
                .maritalStatus(request.maritalStatus())
                .marriageYears(request.marriageYears())
                .unbornChildrenCount(request.unbornChildrenCount())
                .under6ChildrenCount(request.under6ChildrenCount())
                .over7MinorChildrenCount(request.over7MinorChildrenCount())
                .educationStatus(request.educationStatus())
                .hasCar(request.hasCar())
                .carValue(request.carValue())
                .isHouseholdHead(request.isHouseholdHead())
                .isSingle(request.isSingle())
                .fetusCount(request.fetusCount())
                .minorCount(request.minorCount())
                .adultCount(request.adultCount())
                .incomeLevel(request.incomeLevel())
                .housingStatus(request.housingStatus())
                .housingYears(request.housingYears())
                .propertyAsset(request.propertyAsset())
                .carAsset(request.carAsset())
                .financialAsset(request.financialAsset())
                .hasSpecialCategory(request.hasSpecialCategory())
                .build();
    }


    /// 다자녀 계산 로직
    public boolean checkMultiple() {

        int sum = unbornChildrenCount + under6ChildrenCount + over7MinorChildrenCount;

        /// boolean 다자녀
        return sum >= 3;
    }

    /// 총자산 계산 로직
    public long getTotalAsset() {

        return propertyAsset + carAsset + financialAsset;
    }

    /// 전체 세대수 계산 로직
    public int getFamilyCount() {
        return fetusCount + minorCount + adultCount;
    }
}
