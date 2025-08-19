package com.pinHouse.server.platform.domain.diagnosis.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RentalType {

    PERMANENT_RENTAL("영구임대"),
    NATIONAL_RENTAL("국민임대"),
    HAPPY_HOUSING("행복주택"),
    PUBLIC_INTEGRATED("통합공공임대"),
    LONG_TERM_JEONSE("장기전세"),
    PUBLIC_RENTAL("공공임대"),
    PURCHASE_RENTAL("매입임대"),
    JEONSE_RENTAL("전세임대");

    private final String value;

    @JsonGetter
    public String getValue() {
        return value;
    }
}
