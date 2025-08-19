package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.domain.diagnosis.entity.RuleContext;
import com.pinHouse.server.platform.domain.diagnosis.model.RuleResult;
import com.pinHouse.server.platform.domain.diagnosis.model.Severity;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

/** 1) 기초 자격: 세대주 + 무주택 + 재당첨 제한 */
@Component
@Order(1)
public class BaseEligibilityRule implements Rule {
    @Override
    public RuleResult evaluate(RuleContext c) {

        /// 세대주
        if (!c.isHouseholdHead()) {
            return RuleResult.fail(code(), severity(), "세대주가 아니므로 신청 불가", Map.of("householdHead", c.isHouseholdHead()));
        }
        if (!c.isHomeless()) {
            return RuleResult.fail(code(), severity(), "무주택자가 아니므로 신청 불가", Map.of("homeless", c.isHomeless()));
        }
        if (c.isWonBefore() && c.monthsSinceLastWin() < c.getPolicy().reApplyBanMonths()) {
            return RuleResult.fail(code(), severity(), "재당첨 제한 기간 미경과", Map.of("monthsSinceLastWin", c.monthsSinceLastWin()));
        }
        return RuleResult.pass(code(), Severity.INFO, "기초 자격 충족", null);
    }

    @Override public String code() {
        return "BASE_ELIGIBILITY";
    }

    @Override public Severity severity() {
        return Severity.HARD_FAIL;
    }
}
