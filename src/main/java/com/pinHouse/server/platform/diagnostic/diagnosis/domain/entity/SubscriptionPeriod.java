package com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SubscriptionPeriod {

    LESS_THAN_6_MONTHS("6개월 미만",0.5),
    SIX_TO_ONE_YEARS("6개월 이상 ~ 1년 미만",0.8),
    ONE_TO_TWO_YEARS("1년 이상 ~ 2년 미만",1),
    OVER_TWO_YEARS("2년 이상", 2),;

    private final String description;

    private final double years;

    @JsonValue
    public String getDescription() {
        return description;
    }

    public double getYears() {
        return years;
    }

}
