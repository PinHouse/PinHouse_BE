package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.domain.diagnosis.entity.MaritalStatus;
import com.pinHouse.server.platform.domain.diagnosis.entity.Diagnosis;
import com.pinHouse.server.platform.domain.diagnosis.model.RuleResult;
import com.pinHouse.server.platform.domain.diagnosis.model.Severity;
import com.pinHouse.server.platform.domain.diagnosis.model.SupplyType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/** 5) 미성년자 후보 탐색 규칙 */
@Component
@Order(10)
public class YouthSpecialCandidateRule implements Rule {

    @Override
    public RuleResult evaluate(Diagnosis c) {

        /// 미성년자 전용 주택 공급
        boolean ageOk = c.getAge() <= c.getPolicy().youthAgeMin();

        /// 결혼한 미성년자일때
        boolean isMarried = c.isMaritalStatus();

        if (ageOk && isMarried) {

            /// 미성년자 특별공급 후보
            return RuleResult.pass(code(),
                    severity(), "미성년자 특별공급 후보",
                    Map.of("candidate", SupplyType.YOUTH_SPECIAL.name()));
        }

        return RuleResult.pass(code(),
                severity(), "해당 없음",
                Map.of("candidate", false));
    }

    @Override public String code() {
        return "CANDIDATE_YOUTH_SPECIAL";
    }

    @Override public Severity severity() {
        return Severity.INFO;
    }

}
