package com.pinHouse.server.platform.adapter.in.web.dto.request;

import com.pinHouse.server.platform.application.service.SubscriptionAccount;
import com.pinHouse.server.platform.domain.diagnosis.entity.MaritalStatus;
import com.pinHouse.server.platform.domain.diagnosis.entity.SubscriptionPeriod;
import com.pinHouse.server.platform.domain.diagnosis.model.DiagnosisRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DiagnosisRequestDTO {

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
    private boolean maritalStatus;        // 혼인 상태(enum)

    // 5. 부양가족 유무
    private int minorChildrenCount;              // 미성년 자녀 수
    private boolean hasElderDependent;           // 65세 이상 직계존속 부양 여부

    // 6. 특수 대상자 여부
    private boolean isSpecialTarget;             // 국가유공자, 장애인, 북한이탈주민 등 해당 여부

    // [추가된 필드] - RuleContext 의존 필드 반영
    private boolean householdHead;               // 세대주 여부
    private Long housePrice;                      // 분양가/전세가 등
    private String region;                        // 거주 지역 코드 (예: "SUDO", "NON_SUDO")
    private Integer marriageYears;                // 혼인 기간(년)
    private boolean hasAccount;                   // 청약통장 보유 여부
    private SubscriptionPeriod accountYears;                     // 청약통장 가입 기간(년)
    private SubscriptionAccount accountDeposit;                  // 청약통장 예치금(원)
    private String accountType;                   // 청약통장 상품 타입 (예: "SAVING", "DEPOSIT", "INSTALLMENT")
    private boolean localResident;                // 해당 주택 건설지역 내 거주 여부
    private int localResidencyMonths;             // 해당 지역 거주 기간(월)
    private boolean wonBefore;                     // 과거 당첨 여부
    private LocalDate lastWinDate;                 // 마지막 당첨일 (YYYY-MM-DD)


    /// 정적 팩토리 메서드
    public DiagnosisRequest toDomain() {
        return DiagnosisRequest.builder()
                .isHomeless(isHomeless)
                .familyCount(familyCount)
                .incomeRatio(incomeRatio)
                .propertyAsset(propertyAsset)
                .financialAsset(financialAsset)
                .carValue(carValue)
                .age(age)
                .maritalStatus(maritalStatus)
                .minorChildrenCount(minorChildrenCount)
                .hasElderDependent(hasElderDependent)
                .isSpecialTarget(isSpecialTarget)
                .region(region)
                .marriageYears(marriageYears)
                .hasAccount(hasAccount)
                .accountYears(accountYears)
                .accountDeposit(accountDeposit)
                .accountType(accountType)
                .localResident(localResident)
                .localResidencyMonths(localResidencyMonths)
                .wonBefore(wonBefore)
                .lastWinDate(lastWinDate)
                .build();

    }
}
