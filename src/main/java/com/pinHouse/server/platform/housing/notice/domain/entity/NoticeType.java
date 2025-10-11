package com.pinHouse.server.platform.housing.notice.domain.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public enum NoticeType {

    PUBLIC_INTEGRATED("통합공공임대"),
    NATIONAL_RENTAL("국민임대"),
    HAPPY_HOUSING("행복주택"),
    PUBLIC_RENTAL("공공임대"),
    PERMANENT_RENTAL("영구임대"),
    LONG_TERM_JEONSE("장기전세"),
    PURCHASE_RENTAL("매입임대"),
    JEONSE_RENTAL("전세임대"),
    N_RENTAL("N년임대"),
    PRIVATE_RENTAL("민간임대");

    private final String value;

    @JsonGetter
    public String getValue() {
        return value;
    }

    /**
     * 모든 SupplyType enum 반환
     */
    public static List<NoticeType> getAllTypes() {
        return Arrays.asList(NoticeType.values());
    }
}
