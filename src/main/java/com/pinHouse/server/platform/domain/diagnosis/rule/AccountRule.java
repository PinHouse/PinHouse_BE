package com.pinHouse.server.platform.domain.diagnosis.rule;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/** 3) 청약통장 요건(가입기간/예치금/상품유형) */
@Component
@Order(30)
public class AccountRule implements Rule {
    @Override public String code() { return "ACCOUNT_REQUIREMENT"; }
    @Override public Severity severity() { return Severity.HARD_FAIL; }
    @Override
    public RuleResult evaluate(RuleContext c) {
        if (!c.isHasAccount()) {
            return RuleResult.fail(code(), severity(), "청약통장 미보유", Map.of());
        }
        if (c.getAccountYears() < 2) {
            return RuleResult.fail(code(), severity(), "청약통장 2년 이상 가입 필요", Map.of("accountYears", c.getAccountYears()));
        }
        long minDeposit = c.getPolicy().minDepositForHousePrice(Optional.ofNullable(c.getHousePrice()).orElse(0L), c.getRegion(), c.getAccountType());
        if (c.getAccountDeposit() < minDeposit) {
            return RuleResult.fail(code(), severity(), "예치금 기준 미달", Map.of("requiredDeposit", minDeposit, "currentDeposit", c.getAccountDeposit()));
        }
        return RuleResult.pass(code(), Severity.INFO, "청약통장 요건 충족", Map.of("deposit", c.getAccountDeposit()));
    }
}
