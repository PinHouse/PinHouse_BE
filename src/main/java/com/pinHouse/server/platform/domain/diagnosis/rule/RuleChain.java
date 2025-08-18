package com.pinHouse.server.platform.domain.diagnosis.rule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class RuleChain {
    private final List<Rule> rules; // Spring이 @Component Rule들을 주입, @Order로 순서 제어

    public RuleExecutionSummary evaluateAll(RuleContext ctx) {
        RuleExecutionSummary summary = new RuleExecutionSummary();
        for (Rule rule : rules) {
            RuleResult r = rule.evaluate(ctx);
            summary.add(r);
            if (!r.isPass() && rule.severity() == Severity.HARD_FAIL) {
                // 하드 실패면 즉시 중단
                break;
            }
        }
        return summary;
    }
}
