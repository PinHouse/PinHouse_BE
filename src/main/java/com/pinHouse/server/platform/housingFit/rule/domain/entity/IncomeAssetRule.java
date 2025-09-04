package com.pinHouse.server.platform.housingFit.rule.domain.entity;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.repository.EvaluationContext;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
/** 10) 가능한 후보에서 소득/자산 한번에 체크 총량 요건 */
@Component
@Order(10)
public class IncomeAssetRule implements Rule {

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        Diagnosis c = ctx.getDiagnosis();
        /// 이전 후보들 가져오기
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());


        List<SupplyType> eligible = candidates.stream()
                .filter(supply -> {
                    double maxIncome = c.getPolicy().maxIncomeRatio(supply, c.getFamilyCount());
                    long maxAsset = c.getPolicy().maxFinancialAsset(supply, c.getFamilyCount());
                    long maxCar = c.getPolicy().maxCarValue(supply);

                    return c.getIncomeLevel().getPercent() <= maxIncome &&
                            c.getFinancialAsset() <= maxAsset &&
                            c.getCarValue() <= maxCar;
                })
                .toList();


        /// 충족되는 자산이 아니라면,
        if (eligible.isEmpty()) {
            return RuleResult.fail(code(), Severity.HARD_FAIL, "소득/자산 기준 미충족",
                    Map.of("candidate", candidates));
        }

        /// 가능한 결과 리턴
        return RuleResult.pass(code(), Severity.INFO, "소득/자산 요건 충족 후보",
                Map.of("candidate", eligible));
    }

    @Override public String code() {
        return "INCOME_ASSET_LIMIT";
    }

    @Override public Severity severity() {
        return Severity.HARD_FAIL;
    }

}
