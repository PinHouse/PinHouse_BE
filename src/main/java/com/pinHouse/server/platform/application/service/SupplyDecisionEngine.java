package com.pinHouse.server.platform.application.service;

import com.pinHouse.server.platform.domain.diagnosis.entity.Diagnosis;
import com.pinHouse.server.platform.domain.diagnosis.model.SupplyType;
import com.pinHouse.server.platform.domain.diagnosis.rule.RuleExecutionSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class SupplyDecisionEngine {
    private final CandidateExtractor extractor = new CandidateExtractor();

    public SupplyDecision decide(Diagnosis ctx, RuleExecutionSummary summary) {
        // 1) 후보군 뽑기
        List<Candidate> candidates = extractor.extract(summary);

        // 2) 각 후보에 대해 적합도/가점 산정
        List<ScoredCandidate> scored = candidates.stream()
                .map(c -> score(ctx, c))
                .sorted(Comparator.comparingInt(ScoredCandidate::score).reversed())
                .toList();

        // 3) 최상위 선택, 없으면 일반공급으로 폴백
        if (!scored.isEmpty() && scored.get(0).score() >= 0) {
            ScoredCandidate top = scored.get(0);
            return new SupplyDecision(true, top.candidate().type().name(), displayName(top.candidate().type()), top.score(), Map.of(
                    "rankedCandidates", scored.stream().map(s -> Map.of("type", s.candidate().type().name(), "score", s.score())).toList()
            ));
        }

        // 일반공급 점수 산정
        int gScore = generalScore(ctx);
        return new SupplyDecision(true, SupplyType.GENERAL.name(), "일반공급", gScore, Map.of("generalScore", gScore));
    }

    private int generalScore(Diagnosis c) {
        int homelessYears = Math.max(0, (c.getAge() - 20)); // 단순 추정치(실제는 무주택 기간 보유 필요)
        return c.getScoreCalculator().total(new ScoreInput(homelessYears, c.getFamilyCount(), c.getAccountYears()));
    }

    private ScoredCandidate score(Diagnosis c, Candidate candidate) {
        int base = switch (candidate.type()) {
            case YOUTH_SPECIAL -> 20;
            case NEWCOUPLE_SPECIAL -> 18;
            case MULTICHILD_SPECIAL -> 22;
            case ELDER_SUPPORT_SPECIAL -> 16;
            case ELDER_SPECIAL -> 14;
            default -> 0;
        };
        // 가점 일부 반영
        int homelessYears = Math.max(0, (c.getAge() - 20));
        int points = c.getScoreCalculator().total(new ScoreInput(homelessYears, c.getFamilyCount(), c.getAccountYears()));
        int incomeBonus = candidate.incomeOk() ? 5 : -100; // 소득 미충족이면 큰 페널티
        int score = base + (points / 10) + incomeBonus;
        return new ScoredCandidate(candidate, score);
    }

    private String displayName(SupplyType t) {
        return switch (t) {
            case YOUTH_SPECIAL -> "청년 특별공급";
            case NEWCOUPLE_SPECIAL -> "신혼부부 특별공급";
            case MULTICHILD_SPECIAL -> "다자녀 특별공급";
            case ELDER_SUPPORT_SPECIAL -> "노부모 부양 특별공급";
            case ELDER_SPECIAL -> "고령자 특별공급";
            default -> "일반공급";
        };
    }
}
