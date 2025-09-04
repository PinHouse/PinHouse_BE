package com.pinHouse.server.platform.housingFit.rule.domain.entity;

import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RuleExecutionSummary {

    private final List<RuleResult> results = new ArrayList<>();

    public void add(RuleResult r) {
        results.add(r);
    }

    public boolean isHardFailed() {
        return results.stream()
                .anyMatch(r -> !r.pass() && r.severity() == Severity.HARD_FAIL);
    }

    public String primaryFailMessage() {
        return results.stream()
                .filter(r -> !r.pass() && r.severity() == Severity.HARD_FAIL)
                .map(RuleResult::message)
                .findFirst()
                .orElse("자격 요건 미충족");
    }

    public List<DiagnosisDTO.Reason> toReasons() {
        return results.stream()
                .map(r -> new DiagnosisDTO.Reason(r.code(), r.message(), r.details(), r.pass(), r.severity().name()))
                .collect(Collectors.toList());
    }
}
