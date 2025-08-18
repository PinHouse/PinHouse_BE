package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.domain.diagnosis.model.SupplyType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Order(140)
public class ElderCandidateRule implements Rule {
    @Override public String code() { return "CANDIDATE_ELDER_SPECIAL"; }
    @Override public Severity severity() { return Severity.INFO; }
    @Override
    public RuleResult evaluate(RuleContext c) {
        if (c.getAge() >= c.getPolicy().elderAgeMin()) {
            double max = c.getPolicy().maxIncomeRatio(SupplyType.ELDER_SPECIAL, c.getFamilyCount());
            boolean incomeOk = c.getIncomeRatio() <= max;
            return RuleResult.pass(code(), severity(), incomeOk ? "고령자 특별공급 후보" : "고령자: 소득 기준 초과",
                    Map.of("candidate", SupplyType.ELDER_SPECIAL.name(), "incomeOk", incomeOk, "incomeMax", max));
        }
        return RuleResult.pass(code(), severity(), "해당 없음", Map.of("candidate", false));
    }
}
