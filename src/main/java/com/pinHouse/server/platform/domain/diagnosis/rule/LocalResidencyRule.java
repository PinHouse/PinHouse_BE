package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.domain.diagnosis.entity.Diagnosis;
import com.pinHouse.server.platform.domain.diagnosis.model.RuleResult;
import com.pinHouse.server.platform.domain.diagnosis.model.Severity;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

/** 2) 지역 거주 요건 */
@Component
@Order(2)
public class LocalResidencyRule implements Rule {

    @Override
    public RuleResult evaluate(Diagnosis c) {

        /// 해당 지역에 특정기간 거주해야되는지 파악
        int required = c.getPolicy().requiredLocalResidencyMonths(c.getRegion());

        /// 해당 지역에서 특정기간 거주했다면,
        if (c.getLocalResidencyMonths() > required) {

            return RuleResult.pass(code(),
                    Severity.INFO,
                    "지역 거주 요건 충족",
                    Map.of("candidate", c.getRegion().getAddress()));
        }

        /// 특정 지역 우선순위 획득 실패
        return RuleResult.pass(code(),
                Severity.SOFT_WARN,
                "특정 지역 거주 요건 미충족",
                Map.of("requiredMonths", required,
                        "actual", c.getLocalResidencyMonths()));
    }

    @Override public String code() {
        return "LOCAL_RESIDENCY";
    }


    @Override public Severity severity() {
        return Severity.HARD_FAIL;
    }

}
