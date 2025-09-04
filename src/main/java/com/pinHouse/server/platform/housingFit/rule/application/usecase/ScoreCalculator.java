package com.pinHouse.server.platform.housingFit.rule.application.usecase;

import com.pinHouse.server.platform.housingFit.rule.application.service.ScoreInput;

public interface ScoreCalculator {

    int calcHomelessYearsPoint(int years);

    int calcFamilyDependentPoint(int familyCount);

    int calcAccountYearsPoint(int years);

    default int total(ScoreInput in) {
        return calcHomelessYearsPoint(in.homelessYears())
                + calcFamilyDependentPoint(in.familyCount())
                + calcAccountYearsPoint(in.accountYears());
    }

}
