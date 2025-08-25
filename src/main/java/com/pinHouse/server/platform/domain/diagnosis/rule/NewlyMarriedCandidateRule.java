package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.domain.diagnosis.entity.MaritalStatus;
import com.pinHouse.server.platform.domain.diagnosis.entity.Diagnosis;
import com.pinHouse.server.platform.domain.diagnosis.model.RuleResult;
import com.pinHouse.server.platform.domain.diagnosis.model.Severity;
import com.pinHouse.server.platform.domain.diagnosis.model.SupplyType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

/** 6) 신혼 부부 요건 */
@Component
@Order(6)
public class NewlyMarriedCandidateRule implements Rule {

    @Override
    public RuleResult evaluate(Diagnosis c) {



        /// 결혼했는지 체크
        boolean isMarried = c.getMaritalStatus() == MaritalStatus.MARRIED;

        /// 결혼기간 체크
        boolean withinYears = c.getMarriageYears() != null && c.getMarriageYears() <= c.getPolicy().newlyMarriedMaxYears();

        /// 자녀의 나이 계산
        Integer childrenAge = c.getChildrenAge();
        boolean withYouthAge = (childrenAge != null) && (childrenAge <= c.getPolicy().marriedYouthAgeMin());


        /// 신혼부부 특별공급 요건:
        /// - 결혼했고 7년 이하, 또는
        /// - 결혼 7년 초과지만 자녀가 6세 이하인 경우
        if (isMarried && (withinYears || withYouthAge)) {
            double max = c.getPolicy().maxIncomeRatio(SupplyType.NEWCOUPLE_SPECIAL, c.getFamilyCount());
            boolean incomeOk = c.getIncomeRatio() <= max;
            return RuleResult.pass(
                    code(),
                    severity(),
                    incomeOk ? "신혼부부 특별공급 후보" : "신혼부부: 소득 기준 초과",
                    Map.of(
                            "candidate", SupplyType.NEWCOUPLE_SPECIAL.name(),
                            "incomeOk", incomeOk,
                            "incomeMax", max
                    )
            );
        }

        return RuleResult.pass(code(), severity(), "해당 없음", Map.of("candidate", false));
    }

    @Override public String code() {
        return "CANDIDATE_NEWCOUPLE_SPECIAL";
    }

    @Override public Severity severity() {
        return Severity.INFO;
    }

}
