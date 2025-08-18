package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.domain.diagnosis.entity.MaritalStatus;
import com.pinHouse.server.platform.domain.diagnosis.model.SupplyType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/** 5) 특별공급 후보 탐색 규칙 — 각 규칙은 '적합'을 발견하면 INFO로 pass + candidate 표시에 세부값 제공 */
@Component
@Order(100)
public class YouthSpecialCandidateRule implements Rule {
    @Override public String code() { return "CANDIDATE_YOUTH_SPECIAL"; }
    @Override public Severity severity() { return Severity.INFO; }
    @Override
    public RuleResult evaluate(RuleContext c) {
        boolean ageOk = c.getAge() >= c.getPolicy().youthAgeMin() && c.getAge() <= c.getPolicy().youthAgeMax();
        boolean maritalOk = c.getMaritalStatus() == MaritalStatus.SINGLE; // 미혼
        if (ageOk && maritalOk) {
            double max = c.getPolicy().maxIncomeRatio(SupplyType.YOUTH_SPECIAL, c.getFamilyCount());
            boolean incomeOk = c.getIncomeRatio() <= max;
            Map<String,Object> det = new HashMap<>();
            det.put("candidate", SupplyType.YOUTH_SPECIAL.name());
            det.put("incomeOk", incomeOk);
            det.put("incomeMax", max);
            return RuleResult.pass(code(), severity(), incomeOk ? "청년 특별공급 후보" : "청년 특별공급: 소득 기준 초과", det);
        }
        return RuleResult.pass(code(), severity(), "해당 없음", Map.of("candidate", false));
    }
}
