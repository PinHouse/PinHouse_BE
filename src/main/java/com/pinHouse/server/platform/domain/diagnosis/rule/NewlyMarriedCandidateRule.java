package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.domain.diagnosis.entity.MaritalStatus;
import com.pinHouse.server.platform.domain.diagnosis.entity.RuleContext;
import com.pinHouse.server.platform.domain.diagnosis.model.RuleResult;
import com.pinHouse.server.platform.domain.diagnosis.model.Severity;
import com.pinHouse.server.platform.domain.diagnosis.model.SupplyType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Order(6)
public class NewlyMarriedCandidateRule implements Rule {

    @Override
    public RuleResult evaluate(RuleContext c) {
        boolean isMarried = c.getMaritalStatus() == MaritalStatus.MARRIED;
        boolean withinYears = c.getMarriageYears() != null && c.getMarriageYears() <= c.getPolicy().newlyMarriedMaxYears();
        if (isMarried && withinYears) {
            double max = c.getPolicy().maxIncomeRatio(SupplyType.NEWCOUPLE_SPECIAL, c.getFamilyCount());
            boolean incomeOk = c.getIncomeRatio() <= max;
            return RuleResult.pass(code(), severity(), incomeOk ? "신혼부부 특별공급 후보" : "신혼부부: 소득 기준 초과",
                    Map.of("candidate", SupplyType.NEWCOUPLE_SPECIAL.name(), "incomeOk", incomeOk, "incomeMax", max));
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
