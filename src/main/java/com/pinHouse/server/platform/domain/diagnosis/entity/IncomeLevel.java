package com.pinHouse.server.platform.domain.diagnosis.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum IncomeLevel {
    PERCENT_30("1구간"),
    PERCENT_50("2구간"),
    PERCENT_70("3구간"),
    PERCENT_100("4구간"),
    PERCENT_110("5구간"),
    PERCENT_120("5구간"),
    PERCENT_130("5구간"),
    PERCENT_150("6구간"),
    PERCENT_160("기타"),
    PERCENT_170("기타"),
    PERCENT_180("기타"),
    PERCENT_190("기타");

    private final String percent;

    @JsonValue
    public String getPercent() {
        return percent;
    }
}
