package com.pinHouse.server.platform.housingFit.diagnosis.application.dto.request;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.*;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.SubscriptionAccount;
import com.pinHouse.server.platform.user.domain.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
public class DiagnosisRequest {

    /** 1) 기초 자격: 성별 */
    private Gender gender;                                           // 성별

    /**
     * 2) 기초 자격: 나이
     */
    private LocalDate birthday;                                          // 생일

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

    /**
     * 최종) 특수 계층 요건
     */
    private List<SpecialCategory> hasSpecialCategory;                        // 특수 계층인지

}
