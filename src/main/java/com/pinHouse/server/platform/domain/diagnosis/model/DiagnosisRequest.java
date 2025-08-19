package com.pinHouse.server.platform.domain.diagnosis.model;

import com.pinHouse.server.platform.domain.diagnosis.entity.Diagnosis;
import com.pinHouse.server.platform.domain.diagnosis.entity.MaritalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class DiagnosisRequest {

    private boolean isHomeless;                  // 무주택 여부
    private int familyCount;                      // 세대원 수

    // 2. 소득 관련
    private double incomeRatio;                   // 중위소득 대비 비율 (%) 예: 70.0, 100.0

    // 3. 자산 관련
    private final long propertyAsset;                  // 부동산/토지 자산
    private final long financialAsset;                 // 금융자산
    private final long carValue;                       // 자동차가액

    // 4. 연령 및 혼인 상태
    private int age;                             // 만 나이
    private MaritalStatus maritalStatus;        // 혼인 상태(enum)

    // 5. 부양가족 유무
    private int minorChildrenCount;              // 미성년 자녀 수
    private boolean hasElderDependent;           // 65세 이상 직계존속 부양 여부

    // 6. 특수 대상자 여부
    private boolean isSpecialTarget;             // 국가유공자, 장애인, 북한이탈주민 등 해당 여부

    private String region;                        // 거주 지역 코드 (예: "SUDO", "NON_SUDO")
    private Integer marriageYears;                // 혼인 기간(년)
    private boolean hasAccount;                   // 청약통장 보유 여부
    private int accountYears;                     // 청약통장 가입 기간(년)
    private long accountDeposit;                  // 청약통장 예치금(원)
    private String accountType;                   // 청약통장 상품 타입 (예: "SAVING", "DEPOSIT", "INSTALLMENT")
    private boolean localResident;                // 해당 주택 건설지역 내 거주 여부
    private int localResidencyMonths;             // 해당 지역 거주 기간(월)
    private boolean wonBefore;                     // 과거 당첨 여부
    private LocalDate lastWinDate;                 // 마지막 당첨일 (YYYY-MM-DD)

    /// 정적 팩토리 메서드 (도메인으로 변환)
    public Diagnosis toDomain() {
        return Diagnosis.builder()
                .isHomeless(isHomeless)
                .familyCount(familyCount)
                .incomeRatio(incomeRatio)
                .assetValue(propertyAsset)
                .carValue(carValue)
                .age(age)
                .maritalStatus(maritalStatus)
                .minorChildrenCount(minorChildrenCount)
                .hasElderDependent(hasElderDependent)
                .isSpecialTarget(isSpecialTarget)
                .build();
    }
}
