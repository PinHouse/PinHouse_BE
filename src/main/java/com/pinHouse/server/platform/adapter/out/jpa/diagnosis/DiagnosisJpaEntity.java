package com.pinHouse.server.platform.adapter.out.jpa.diagnosis;

import com.pinHouse.server.platform.adapter.out.jpa.BaseTimeEntity;
import com.pinHouse.server.platform.adapter.out.jpa.user.UserJpaEntity;
import com.pinHouse.server.platform.domain.diagnosis.entity.Diagnosis;
import com.pinHouse.server.platform.domain.diagnosis.entity.MaritalStatus;
import com.pinHouse.server.platform.domain.user.User;
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

    private boolean isHomeless;                  // 무주택 여부
    private int familyCount;                      // 세대원 수
    private double incomeRatio;                   // 중위소득 대비 비율 (%) 예: 70.0, 100.0
    private double assetValue;                    // 부동산, 금융자산 가치 (만원 단위)
    private double carValue;                      // 자동차 가액 (만원 단위)
    private int age;                             // 만 나이
    private MaritalStatus maritalStatus;        // 혼인 상태(enum)
    private int minorChildrenCount;              // 미성년 자녀 수
    private boolean hasElderDependent;           // 65세 이상 직계존속 부양 여부
    private boolean isSpecialTarget;             // 국가유공자, 장애인, 북한이탈주민 등 해당 여부

    /// 정적 팩토리 메서드
    public static DiagnosisJpaEntity from(Diagnosis diagnosis) {
        return DiagnosisJpaEntity.builder()
                .user(diagnosis.getUserId())
                .familyCount(diagnosis.getFamilyCount())
                .incomeRatio(diagnosis.getIncomeRatio())
                .assetValue(diagnosis.getAssetValue())
                .carValue(diagnosis.getCarValue())
                .age(diagnosis.getAge())
                .maritalStatus(diagnosis.getMaritalStatus())
                .minorChildrenCount(diagnosis.getMinorChildrenCount())
                .hasElderDependent(diagnosis.isHasElderDependent())
                .isSpecialTarget(diagnosis.isSpecialTarget())
                .build();
    }

    /// toDomain
    public Diagnosis toDomain() {
        return Diagnosis.builder()
                .id(id)
                .userId(user)
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
