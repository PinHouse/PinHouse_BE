package com.pinHouse.server.platform.housingFit.rule.domain.rule;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.Severity;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.SupplyType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

/** 7) 다자녀 요건 */
@Component
@Order(7)
public class MultiChildCandidateRule implements Rule {

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        Diagnosis c = ctx.getDiagnosis();
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());

        /// 자녀가 3명 이상이라면 후보 제공
        if (c.getMinorChildrenCount() >= 3) {

            /// 없었다면 추가
            if (!candidates.contains(SupplyType.MULTICHILD_SPECIAL)) {
                candidates.add(SupplyType.MULTICHILD_SPECIAL);
            }

            return RuleResult.pass(code(),
                    severity(),
                    "다자녀 특별공급 후보",
                    Map.of("candidate", candidates));
        }

        return RuleResult.pass(code(), severity(), "다자녀 특별공급 해당 없음", Map.of("candidate", candidates));
    }

    @Override public String code() {
        return "CANDIDATE_MULTICHILD_SPECIAL";
    }
}
