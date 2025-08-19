package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.domain.diagnosis.entity.MaritalStatus;
import com.pinHouse.server.platform.domain.diagnosis.entity.RuleContext;
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
    public RuleResult evaluate(RuleContext c) {

        /// 미성년자 전용 주택 공급
        boolean ageOk = c.getAge() <= c.getPolicy().youthAgeMin();

        /// 결혼한 미성년자일때
        boolean maritalOk = c.getMaritalStatus() == MaritalStatus.MINOR_PARENT;

        if (ageOk && maritalOk) {

            /// 소득 기준 체크
            double max = c.getPolicy().maxIncomeRatio(SupplyType.YOUTH_SPECIAL, c.getFamilyCount());
            boolean incomeOk = c.getIncomeRatio() <= max;
            Map<String,Object> det = new HashMap<>();
            det.put("candidate", SupplyType.YOUTH_SPECIAL.name());
            det.put("incomeOk", incomeOk);
            det.put("incomeMax", max);
            return RuleResult.pass(code(), severity(), incomeOk ? "미성년자 특별공급 후보" : "미성년자 특별공급: 소득 기준 초과", det);
        }
        return RuleResult.pass(code(), severity(), "해당 없음", Map.of("candidate", false));
    }

    @Override public String code() {
        return "CANDIDATE_YOUTH_SPECIAL";
    }

    @Override public Severity severity() {
        return Severity.INFO;
    }

}
