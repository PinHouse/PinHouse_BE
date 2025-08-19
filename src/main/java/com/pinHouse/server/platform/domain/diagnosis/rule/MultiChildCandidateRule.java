package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.domain.diagnosis.entity.RuleContext;
import com.pinHouse.server.platform.domain.diagnosis.model.RuleResult;
import com.pinHouse.server.platform.domain.diagnosis.model.Severity;
import com.pinHouse.server.platform.domain.diagnosis.model.SupplyType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Order(7)
public class MultiChildCandidateRule implements Rule {

    @Override
    public RuleResult evaluate(RuleContext c) {
        if (c.getMinorChildrenCount() >= 3) {
            double max = c.getPolicy().maxIncomeRatio(SupplyType.MULTICHILD_SPECIAL, c.getFamilyCount());
            boolean incomeOk = c.getIncomeRatio() <= max;
            return RuleResult.pass(code(), severity(), incomeOk ? "다자녀 특별공급 후보" : "다자녀: 소득 기준 초과",
                    Map.of("candidate", SupplyType.MULTICHILD_SPECIAL.name(), "incomeOk", incomeOk, "incomeMax", max));
        }
        return RuleResult.pass(code(), severity(), "해당 없음", Map.of("candidate", false));
    }

    @Override public String code() {
        return "CANDIDATE_MULTICHILD_SPECIAL";
    }

    @Override public Severity severity() {
        return Severity.INFO;
    }

}
