package com.pinHouse.server.platform.domain.diagnosis.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum IncomeLevel {
    PERCENT_30("1구간",30),
    PERCENT_50("2구간",50),
    PERCENT_70("3구간", 70),
    PERCENT_100("4구간", 100),
    PERCENT_110("5구간", 110),
    PERCENT_120("5구간", 120),
    PERCENT_130("5구간", 130),
    PERCENT_150("6구간", 150),
    PERCENT_160("기타", 160),
    PERCENT_170("기타", 170),
    PERCENT_180("기타", 180),
    PERCENT_190("기타", 190),;

    private final String value;
    private final int percent;

    @JsonValue
    public String getValue() {
        return value;
    }

    public int getPercent() {
        return percent;
    }
}
