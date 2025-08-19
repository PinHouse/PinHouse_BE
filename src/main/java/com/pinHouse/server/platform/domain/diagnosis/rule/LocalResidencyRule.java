package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.domain.diagnosis.entity.RuleContext;
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
    public RuleResult evaluate(RuleContext c) {

        /// 해당 지역에 몇 년 거주해야되는지 존재
        int required = c.getPolicy().requiredLocalResidencyMonths(c.getRegion());

        if (!c.isLocalResident() || c.getLocalResidencyMonths() < required) {
            return RuleResult.fail(code(), severity(), "해당 지역 거주 요건 미충족",
                    Map.of("requiredMonths", required, "actual", c.getLocalResidencyMonths(), "localResident", c.isLocalResident()));
        }
        return RuleResult.pass(code(), Severity.INFO, "지역 거주 요건 충족", Map.of("months", c.getLocalResidencyMonths()));
    }

    @Override public String code() {
        return "LOCAL_RESIDENCY";
    }


    @Override public Severity severity() {
        return Severity.HARD_FAIL;
    }

}
