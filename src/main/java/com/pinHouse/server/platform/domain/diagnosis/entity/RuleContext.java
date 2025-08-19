package com.pinHouse.server.platform.domain.diagnosis.entity;

import com.pinHouse.server.platform.adapter.out.DefaultScoreCalculator;
import com.pinHouse.server.platform.adapter.out.InMemoryPolicyProvider;
import com.pinHouse.server.platform.application.out.diagnosis.PolicyProvider;
import com.pinHouse.server.platform.application.out.diagnosis.ScoreCalculator;
import com.pinHouse.server.platform.application.service.*;
import com.pinHouse.server.platform.domain.diagnosis.model.DiagnosisRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;

@Getter
@Builder
@AllArgsConstructor
public class RuleContext {

    /** 1) 기초 자격: 나이 + 무주택 */
    private final int age;                             // 나이
    private final boolean homeless;                    // 무주택 여부

    /** 2) 지역 거주 요건 */
    private final RegionCode region;                   // 거주지역/해당지역 내 여부
    private final boolean localResident;               // 해당 주택건설지역 내 거주
    private final int localResidencyMonths;            // 거주기간(월)

    /** 3) 청약통장 요건(가입기간/예치금/상품유형) */
    private final boolean hasAccount;
    private final int accountYears;                    // 가입년수(년)
    private final long accountDeposit;                 // 예치금
    private final AccountType accountType;             // 예: 청약저축/청약예금/청약부금

    /** 4) 소득/자산 총량 요건(공급유형 공통 상한) */
    private final long propertyAsset;                  // 부동산/토지 자산
    private final long financialAsset;                 // 금융자산
    private final long carValue;                       // 자동차가액

    private final int familyCount;                     // 가구원수(세대원)
    private final double incomeRatio;                  // 도시근로자 대비 소득비율(%)

    /** 5) 특별공급 후보 탐색

    /** 6) 신혼 부부 요건 */
    private final MaritalStatus maritalStatus;
    private final Integer marriageYears;
    private final int childrenAge;

    /** 7) 다자녀 요건 */
    private final int minorChildrenCount;

    /** 8) 노부모 부양 지원 */
    private final boolean hasElderDependent;           // 65세 이상 직계존속 3년 이상 부양 여부

    /** 9) 고령자 제한 */

    // 과거 당첨/재당첨 제한
    private final boolean wonBefore;
    private final LocalDate lastWinDate;

    // 정책/계산기 접근자
    @Builder.Default private final PolicyProvider policy = new InMemoryPolicyProvider();
    @Builder.Default private final ScoreCalculator scoreCalculator = new DefaultScoreCalculator();

    public static RuleContext from(DiagnosisRequest req) {
        return RuleContext.builder()
                .age(req.getAge())
                .homeless(req.isHomeless())
                .familyCount(req.getFamilyCount())
                .incomeRatio(req.getIncomeRatio())
                .region(RegionCode.from(req.getRegion()))
                .maritalStatus(req.getMaritalStatus())
                .marriageYears(req.getMarriageYears())
                .minorChildrenCount(req.getMinorChildrenCount())
                .hasElderDependent(req.isHasElderDependent())
                .hasAccount(req.isHasAccount())
                .accountYears(req.getAccountYears())
                .accountDeposit(req.getAccountDeposit())
                .accountType(AccountType.from(req.getAccountType()))
                .propertyAsset(req.getPropertyAsset())
                .financialAsset(req.getFinancialAsset())
                .carValue(req.getCarValue())
                .localResident(req.isLocalResident())
                .localResidencyMonths(req.getLocalResidencyMonths())
                .wonBefore(req.isWonBefore())
                .build();
    }

    public int monthsSinceLastWin() {
        if (lastWinDate == null) return Integer.MAX_VALUE;
        return Period.between(lastWinDate, LocalDate.now()).getYears() * 12
                + Period.between(lastWinDate, LocalDate.now()).getMonths();
    }
}
