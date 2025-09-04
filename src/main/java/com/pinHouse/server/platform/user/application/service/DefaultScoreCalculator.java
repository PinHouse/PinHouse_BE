package com.pinHouse.server.platform.adapter.out;


import com.pinHouse.server.platform.housingFit.rule.application.usecase.ScoreCalculator;

public class DefaultScoreCalculator implements ScoreCalculator {
    public int calcHomelessYearsPoint(int years) {
        return Math.min(32, Math.max(0, years * 2));
    }
    public int calcFamilyDependentPoint(int familyCount) {
        // 본인 제외 부양가족 수 기준이라고 가정 — 단순화 예시
        int dependents = Math.max(0, familyCount - 1);
        return Math.min(35, dependents * 5);
    }
    public int calcAccountYearsPoint(int years) {
        return Math.min(17, years + 5);
    }
}
