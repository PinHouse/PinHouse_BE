package com.pinHouse.server.platform.housingFit.rule.application.service;

import com.pinHouse.server.platform.housingFit.rule.domain.entity.SupplyType;

import java.util.List;
import java.util.stream.Collectors;

public class CandidateExtractor {
    List<Candidate> extract(RuleExecutionSummary summary) {
        return summary.getResults().stream()
                .filter(r -> r.code().startsWith("CANDIDATE_") && r.pass())
                .map(r -> new Candidate(
                        SupplyType.valueOf(String.valueOf(r.details().getOrDefault("candidate", SupplyType.GENERAL.name()))),
                        Boolean.TRUE.equals(r.details().get("incomeOk"))
                ))
                .filter(c -> c.type() != SupplyType.GENERAL)
                .distinct()
                .collect(Collectors.toList());
    }
}
