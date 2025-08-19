package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.domain.diagnosis.entity.RuleContext;
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
    private final List<Rule> rules; // Spring이 @Component Rule들을 주입, @Order로 순서 제어

    public RuleExecutionSummary evaluateAll(RuleContext ctx) {
        RuleExecutionSummary summary = new RuleExecutionSummary();
        log.info("RuleChain rules size: " + rules.size());

        for (Rule rule : rules) {
            RuleResult r = rule.evaluate(ctx);
            summary.add(r);
            if (!r.pass() && rule.severity() == Severity.HARD_FAIL) {
                // 하드 실패면 즉시 중단
                break;
            }
        }
        return summary;
    }
}
