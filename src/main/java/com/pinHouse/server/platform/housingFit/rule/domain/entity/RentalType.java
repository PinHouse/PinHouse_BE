package com.pinHouse.server.platform.housingFit.rule.domain.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public enum RentalType {

    PUBLIC_INTEGRATED("통합공공임대"),
    NATIONAL_RENTAL("국민임대"),
    HAPPY_HOUSING("행복주택"),
    PUBLIC_RENTAL("공공임대"),
    PERMANENT_RENTAL("영구임대"),
    LONG_TERM_JEONSE("장기전세"),
    PURCHASE_RENTAL("매입임대"),
    JEONSE_RENTAL("전세임대");

    private final String value;

    @JsonGetter
    public String getValue() {
        return value;
    }

    /**
     * 모든 SupplyType enum 반환
     */
    public static List<RentalType> getAllTypes() {
        return Arrays.asList(RentalType.values());
    }
}
