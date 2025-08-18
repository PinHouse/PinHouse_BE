package com.pinHouse.server.platform.domain.diagnosis.entity;

import com.pinHouse.server.platform.domain.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Diagnosis extends BaseDomain {

    private Long id;

    private Long userId;
    // 1. 기본 정보
    private boolean isHomeless;                  // 무주택 여부
    private int familyCount;                      // 세대원 수

    // 2. 소득 관련
    private double incomeRatio;                   // 중위소득 대비 비율 (%) 예: 70.0, 100.0

    // 3. 자산 관련
    private double assetValue;                    // 부동산, 금융자산 가치 (만원 단위)
    private double carValue;                      // 자동차 가액 (만원 단위)

    // 4. 연령 및 혼인 상태
    private int age;                             // 만 나이
    private MaritalStatus maritalStatus;        // 혼인 상태(enum)

    // 5. 부양가족 유무
    private int minorChildrenCount;              // 미성년 자녀 수
    private boolean hasElderDependent;           // 65세 이상 직계존속 부양 여부

    // 6. 특수 대상자 여부
    private boolean isSpecialTarget;             // 국가유공자, 장애인, 북한이탈주민 등 해당 여부

    /// 정적 팩토리 메서드
    public static Diagnosis of(boolean isHomeless, int familyCount, double incomeRatio, double assetValue, double carValue, int age, MaritalStatus maritalStatus, int minorChildrenCount, boolean hasElderDependent, boolean isSpecialTarget) {
        return Diagnosis.builder()
                .isHomeless(isHomeless)
                .familyCount(familyCount)
                .incomeRatio(incomeRatio)
                .assetValue(assetValue)
                .carValue(carValue)
                .age(age)
                .maritalStatus(maritalStatus)
                .minorChildrenCount(minorChildrenCount)
                .hasElderDependent(hasElderDependent)
                .isSpecialTarget(isSpecialTarget)
                .build();
    }

}
