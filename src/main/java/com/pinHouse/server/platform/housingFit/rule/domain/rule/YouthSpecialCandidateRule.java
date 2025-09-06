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

/** 5) 미성년자 후보 탐색 규칙 */
@Component
@Order(10)
public class YouthSpecialCandidateRule implements Rule {

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        Diagnosis c = ctx.getDiagnosis();
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());

        /// 미성년자 전용 주택 공급
        boolean ageOk = c.getAge() <= c.getPolicy().youthAgeMin();

        /// 결혼한 미성년자일때
        boolean isMarried = c.isMaritalStatus();

        if (ageOk && isMarried) {

            if (!candidates.contains(SupplyType.YOUTH_SPECIAL)) {
                candidates.add(SupplyType.YOUTH_SPECIAL);
            }

            /// 미성년자 특별공급 후보
            return RuleResult.pass(code(),
                    severity(), "미성년자 특별공급 후보",
                    Map.of("candidate", SupplyType.YOUTH_SPECIAL.name()));
        }

        return RuleResult.pass(code(),
                severity(), "해당 없음",
                Map.of("candidate", candidates));
    }

    @Override public String code() {
        return "CANDIDATE_YOUTH_SPECIAL";
    }
}
