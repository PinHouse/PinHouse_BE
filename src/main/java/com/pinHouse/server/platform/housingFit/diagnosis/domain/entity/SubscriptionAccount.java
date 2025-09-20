package com.pinHouse.server.platform.housingFit.diagnosis.domain.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SubscriptionAccount {
    UNDER_600("600만원 이하"),
    UPPER_600("600만원 이상");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

}

