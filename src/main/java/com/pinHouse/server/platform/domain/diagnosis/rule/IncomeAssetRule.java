package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.domain.diagnosis.entity.RuleContext;
import com.pinHouse.server.platform.domain.diagnosis.model.RuleResult;
import com.pinHouse.server.platform.domain.diagnosis.model.Severity;
import com.pinHouse.server.platform.domain.diagnosis.model.SupplyType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

/** 3) 소득/자산 총량 요건(공급유형 공통 상한) */
@Component
@Order(4)
public class IncomeAssetRule implements Rule {

    @Override
    public RuleResult evaluate(RuleContext c) {
        double maxIncomeGeneral = c.getPolicy().maxIncomeRatio(SupplyType.GENERAL, c.getFamilyCount());
        if (c.getIncomeRatio() > maxIncomeGeneral) {
            return RuleResult.fail(code(), severity(), "소득 기준 초과(일반공급 상한)", Map.of("max", maxIncomeGeneral, "actual", c.getIncomeRatio()));
        }
        long maxAsset = c.getPolicy().maxFinancialAsset(SupplyType.GENERAL, c.getFamilyCount());
        if (c.getFinancialAsset() > maxAsset) {
            return RuleResult.fail(code(), severity(), "금융자산 기준 초과", Map.of("max", maxAsset, "actual", c.getFinancialAsset()));
        }
        if (c.getCarValue() > c.getPolicy().maxCarValue(SupplyType.GENERAL)) {
            return RuleResult.fail(code(), severity(), "자동차가액 기준 초과", Map.of("max", c.getPolicy().maxCarValue(SupplyType.GENERAL), "actual", c.getCarValue()));
        }
        return RuleResult.pass(code(), Severity.INFO, "소득/자산 요건 충족", null);
    }

    @Override public String code() {
        return "INCOME_ASSET_LIMIT";
    }

    @Override public Severity severity() {
        return Severity.HARD_FAIL;
    }

}
