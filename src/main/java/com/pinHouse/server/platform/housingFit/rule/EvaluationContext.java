package com.pinHouse.server.platform.housingFit.rule;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.SupplyType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class EvaluationContext {
    private final Diagnosis diagnosis;
    private final List<RuleResult> ruleResults = new ArrayList<>();
    private List<SupplyType> currentCandidates = List.of();

    public EvaluationContext(Diagnosis diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void addResult(RuleResult result) {
        ruleResults.add(result);

        // 후보 리스트가 있으면 업데이트
        if (result.details().containsKey("candidate")) {
            currentCandidates = (List<SupplyType>) result.details().get("candidate");
        }
    }
}
