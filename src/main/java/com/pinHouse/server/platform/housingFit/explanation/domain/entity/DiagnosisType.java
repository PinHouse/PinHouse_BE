package com.pinHouse.server.platform.housingFit.explanation.domain.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DiagnosisType {

    YOUTH("청년"),
    MIDDLE_AGED("중장년층"),
    SENIOR("고령");

    private final String description;

    @JsonValue
    public String getDescription() {
        return description;
    }
}

