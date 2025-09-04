package com.pinHouse.server.platform.housingFit.rule.domain.rule;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.housingFit.rule.EvaluationContext;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.Rule;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.Severity;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.SupplyType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

import static com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.FamilySituation.SUPPORTING_ELDERLY;

/** 8) 노부모 부양 지원 */
@Component
@Order(8)
public class ElderSupportCandidateRule implements Rule {

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        Diagnosis c = ctx.getDiagnosis();
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());

        /// 노부모가 있는지 체크
        if (c.getFamilySituation().equals(SUPPORTING_ELDERLY)) {

            /// 없었다면 추가
            if (!candidates.contains(SupplyType.ELDER_SUPPORT_SPECIAL)) {
                candidates.add(SupplyType.ELDER_SUPPORT_SPECIAL);
            }

            /// 조건 충족으로 넘어간다.
            return RuleResult.pass(code(), severity(), "노부모 부양 특별공급 후보",
                    Map.of("candidate", candidates));
        }

        /// 조건이 없으므로 넘어간다.
        return RuleResult.pass(code(), severity(), "해당 없음", Map.of("candidate", candidates));
    }

    @Override public String code() {
        return "CANDIDATE_ELDER_SUPPORT_SPECIAL";
    }

    @Override public Severity severity() {
        return Severity.INFO;
    }

}
