package com.pinHouse.server.platform.housingFit.diagnosis.domain.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SubscriptionPaymentCount {

    FROM_0_TO_5("0회 ~ 5회"),
    FROM_6_TO_11("6회 ~ 11회"),
    FROM_11_TO_24("12회 ~ 23회"),
    OVER_24("24회 ~ 35회"),
    FROM_36_TO_48("36회  ~ 48회"),
    FROM_48_TO_60("49회 ~ 59회"),
    OVER_60("60회 이상");

    private final String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

}
