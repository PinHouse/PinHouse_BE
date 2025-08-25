package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.domain.diagnosis.entity.Diagnosis;
import com.pinHouse.server.platform.domain.diagnosis.model.RuleResult;
import com.pinHouse.server.platform.domain.diagnosis.model.Severity;
import com.pinHouse.server.platform.domain.diagnosis.model.SupplyType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

/** 9) 고령자 제한 */
@Component
@Order(9)
public class ElderCandidateRule implements Rule {

    @Override
    public RuleResult evaluate(Diagnosis c) {

        /// 나이가 고령자 제한이 되는지 체크
        if (c.getAge() >= c.getPolicy().elderAgeMin()) {

            /// 나이가 고령자이기에 후보로 지정
            return RuleResult.pass(code(), severity(), "고령자 특별공급 후보",
                    Map.of("candidate", SupplyType.ELDER_SPECIAL));
        }

        return RuleResult.pass(code(), severity(), "해당 없음", Map.of("candidate", false));
    }

    @Override public String code() {
        return "CANDIDATE_ELDER_SPECIAL";
    }

    @Override public Severity severity() {
        return Severity.INFO;
    }

}
