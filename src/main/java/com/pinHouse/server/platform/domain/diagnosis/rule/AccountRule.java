package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.domain.diagnosis.entity.RuleContext;
import com.pinHouse.server.platform.domain.diagnosis.model.RuleResult;
import com.pinHouse.server.platform.domain.diagnosis.model.Severity;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

/** 3) 청약통장 요건(가입기간/예치금/상품유형) */
@Component
@Order(3)
public class AccountRule implements Rule {

    @Override
    public RuleResult evaluate(RuleContext c) {

        /// 청약 통장이 없다면
        if (!c.isHasAccount()) {
            return RuleResult.fail(code(), severity(), "청약통장 미보유", Map.of());
        }

        /// 청약 통장의 기간이 짧다면
        if (c.getAccountYears() < 2) {
            return RuleResult.fail(code(), severity(), "청약통장 2년 이상 가입 필요", Map.of("accountYears", c.getAccountYears()));
        }

        /// 추천 가능한 주택 규모 계산
        int recommendedSize = c.getPolicy().recommendHousingSize(c.getRegion(), c.getAccount(), c.getAccountDeposit());

        /// 최소 예치금 미달 시
        if (recommendedSize == 0) {
            return RuleResult.fail(
                    code(),
                    severity(),
                    "예치금 기준 미달",
                    Map.of("requiredDeposit", c.getAccountDeposit(), "currentDeposit", c.getAccountDeposit())
            );
        }

        /// 예치금 이상이면 추천 주택 규모 포함
        return RuleResult.pass(
                code(),
                Severity.INFO,
                "청약통장 요건 충족 - 추천 주택 규모: " + recommendedSize + "㎡ 이하",
                Map.of("deposit", c.getAccountDeposit(), "recommendedHousingSize", recommendedSize)
        );

    }

    @Override public String code() {
        return "ACCOUNT_REQUIREMENT";
    }

    @Override public Severity severity() {
        return Severity.HARD_FAIL;
    }

}
