package com.pinHouse.server.platform.application.service;

import com.pinHouse.server.platform.domain.diagnosis.rule.RuleExecutionSummary;

import java.util.List;
import java.util.stream.Collectors;

public class CandidateExtractor {
    List<Candidate> extract(RuleExecutionSummary summary) {
        return summary.getResults().stream()
                .filter(r -> r.getCode().startsWith("CANDIDATE_") && r.isPass())
                .map(r -> new Candidate(
                        SupplyType.valueOf(String.valueOf(r.getDetails().getOrDefault("candidate", SupplyType.GENERAL.name()))),
                        Boolean.TRUE.equals(r.getDetails().get("incomeOk"))
                ))
                .filter(c -> c.type() != SupplyType.GENERAL)
                .distinct()
                .collect(Collectors.toList());
    }
}
