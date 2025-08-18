package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.adapter.in.web.dto.response.DiagnosisDTO;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RuleExecutionSummary {
    private final List<RuleResult> results = new ArrayList<>();

    public void add(RuleResult r) { results.add(r); }

    public boolean isHardFailed() {
        return results.stream().anyMatch(r -> !r.isPass() && r.getSeverity() == Severity.HARD_FAIL);
    }

    public String primaryFailMessage() {
        return results.stream()
                .filter(r -> !r.isPass() && r.getSeverity() == Severity.HARD_FAIL)
                .map(RuleResult::getMessage)
                .findFirst()
                .orElse("자격 요건 미충족");
    }

    public List<DiagnosisDTO.Reason> toReasons() {
        return results.stream()
                .map(r -> new DiagnosisDTO.Reason(r.getCode(), r.getMessage(), r.getDetails(), r.isPass(), r.getSeverity().name()))
                .collect(Collectors.toList());
    }
}
