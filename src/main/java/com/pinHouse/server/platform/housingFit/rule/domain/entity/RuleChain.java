package com.pinHouse.server.platform.housingFit.rule.domain.entity;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.repository.EvaluationContext;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class RuleChain {
    private final List<Rule> rules;

    public RuleExecutionSummary evaluateAll(Diagnosis diagnosis) {
        EvaluationContext context = new EvaluationContext(diagnosis);
        RuleExecutionSummary summary = new RuleExecutionSummary();

        for (Rule rule : rules) {
            RuleResult result = rule.evaluate(context); // 자신만 평가
            context.addResult(result);                   // 최신 후보 업데이트
            summary.add(result);

            // HARD_FAIL이면 즉시 종료
            if (!result.pass() && result.severity() == Severity.HARD_FAIL) {
                break;
            }
        }

        return summary;
    }

}
