package com.pinHouse.server.platform.housingFit.rule.domain.entity;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.housingFit.rule.EvaluationContext;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

/** 9) 고령자 제한 */
@Component
@Order(9)
public class ElderCandidateRule implements Rule {

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        Diagnosis c = ctx.getDiagnosis();
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());


        /// 나이가 고령자 제한이 되는지 체크
        if (c.getAge() >= c.getPolicy().elderAgeMin()) {

            /// 없었다면 추가 (예외처리)
            if (!candidates.contains(SupplyType.ELDER_SPECIAL)) {
                candidates.add(SupplyType.ELDER_SPECIAL);
            }

            /// 나이가 고령자이기에 후보로 지정
            return RuleResult.pass(code(), severity(), "고령자 특별공급 후보",
                    Map.of("candidate", candidates));
        }
        /// 나이가 안되기에 삭제
        candidates.remove(SupplyType.ELDER_SPECIAL);

        return RuleResult.pass(code(), severity(), "고령자 해당 없음", Map.of("candidate", candidates));
    }

    @Override public String code() {
        return "CANDIDATE_ELDER_SPECIAL";
    }

    @Override public Severity severity() {
        return Severity.INFO;
    }

}
