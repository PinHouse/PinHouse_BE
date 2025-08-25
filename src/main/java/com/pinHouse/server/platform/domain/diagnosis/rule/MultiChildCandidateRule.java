package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.domain.diagnosis.entity.Diagnosis;
import com.pinHouse.server.platform.domain.diagnosis.model.RuleResult;
import com.pinHouse.server.platform.domain.diagnosis.model.Severity;
import com.pinHouse.server.platform.domain.diagnosis.model.SupplyType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

/** 7) 다자녀 요건 */
@Component
@Order(7)
public class MultiChildCandidateRule implements Rule {

    @Override
    public RuleResult evaluate(Diagnosis c) {

        /// 자녀가 3명 이상이라면 후보 제공
        if (c.getMinorChildrenCount() >= 3) {

            return RuleResult.pass(code(),
                    severity(),
                    "다자녀 특별공급 후보",
                    Map.of("candidate", SupplyType.MULTICHILD_SPECIAL));
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
