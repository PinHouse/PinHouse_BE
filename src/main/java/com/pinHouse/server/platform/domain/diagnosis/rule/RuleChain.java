package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.domain.diagnosis.entity.Diagnosis;
import com.pinHouse.server.platform.domain.diagnosis.model.RuleResult;
import com.pinHouse.server.platform.domain.diagnosis.model.Severity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class RuleChain {
    private final List<Rule> rules;

    public RuleExecutionSummary evaluateAll(Diagnosis ctx) {
        RuleExecutionSummary summary = new RuleExecutionSummary();
        log.info("RuleChain rules size: " + rules.size());

        for (Rule rule : rules) {
            RuleResult r = rule.evaluate(ctx);
            summary.add(r);

            /// HARD_FAIL 이면, 진단을 바로 종료
            if (!r.pass() && rule.severity() == Severity.HARD_FAIL) {
                break;
            }
        }
        return summary;
    }
}
