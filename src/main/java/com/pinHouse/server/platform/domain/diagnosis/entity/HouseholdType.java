package com.pinHouse.server.platform.domain.diagnosis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HouseholdType {

    SINGLE_PARENT("한부모 가정"),
    PROTECTED_SINGLE_PARENT("보호대상 한부모 가정"),
    RELATIVE_FOSTER("친인척 위탁가정"),
    SUBSTITUTE_CARE("대리양육가정");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
