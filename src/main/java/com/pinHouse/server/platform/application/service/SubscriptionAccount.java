package com.pinHouse.server.platform.application.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SubscriptionAccount {
    UNDER_600("600만원 이하"),
    UPPER_600("600만원 이상");

    private final String value;

    @JsonIgnore
    public String getValue() {
        return value;
    }

}

