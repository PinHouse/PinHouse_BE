package com.pinHouse.server.platform.housingFit.rule.domain.entity;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.housingFit.rule.EvaluationContext;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** 3) 청약통장 요건(가입기간/예치금/상품유형) */
@Component
@Order(3)
public class AccountRule implements Rule {

    private static final List<RentalType> NO_ACCOUNT_ALLOWED = List.of(
            RentalType.PERMANENT_RENTAL,  // 영구임대
            RentalType.HAPPY_HOUSING,      // 행복주택
            RentalType.PURCHASE_RENTAL,  // 매입임대
            RentalType.JEONSE_RENTAL      // 전세임대
    );

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        Diagnosis c = ctx.getDiagnosis();
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());

        // 청약통장 미보유
        if (!c.isHasAccount()) {
            return RuleResult.fail(code(), severity(),
                    "청약통장 미보유",
                    Map.of("candidate", candidates));
        }

        // 가입 기간 체크
        if (c.getAccountYears().getYears() < 2) {
            return RuleResult.fail(code(), severity(),
                    "청약통장 2년 이상 가입 필요",
                    Map.of("accountYears", c.getAccountYears()));
        }

        // 조건 통과
        return RuleResult.pass(code(), Severity.INFO, "청약통장 요건 충족", Map.of("accountYears", c.getAccountYears()));


    }

    @Override public String code() {
        return "ACCOUNT_REQUIREMENT";
    }

    @Override public Severity severity() {
        return Severity.HARD_FAIL;
    }

}
