package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.domain.diagnosis.entity.Diagnosis;
import com.pinHouse.server.platform.domain.diagnosis.model.RuleResult;
import com.pinHouse.server.platform.domain.diagnosis.model.Severity;
import com.pinHouse.server.platform.domain.diagnosis.model.SupplyType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.pinHouse.server.platform.domain.diagnosis.entity.FamilySituation.SUPPORTING_ELDERLY;

/** 8) 노부모 부양 지원 */
@Component
@Order(8)
public class ElderSupportCandidateRule implements Rule {

    @Override
    public RuleResult evaluate(Diagnosis c) {

        /// 노부모가 있는지 체크
        if (c.getFamilySituation().equals(SUPPORTING_ELDERLY)) {

            /// 조건 충족으로 넘어간다.
            return RuleResult.pass(code(), severity(), "노부모 부양 특별공급 후보",
                    Map.of("candidate", SupplyType.ELDER_SUPPORT_SPECIAL));
        }

        /// 조건이 없으므로 넘어간다.
        return RuleResult.pass(code(), severity(), "해당 없음", Map.of("candidate", false));
    }

    @Override public String code() {
        return "CANDIDATE_ELDER_SUPPORT_SPECIAL";
    }

    @Override public Severity severity() {
        return Severity.INFO;
    }

}
