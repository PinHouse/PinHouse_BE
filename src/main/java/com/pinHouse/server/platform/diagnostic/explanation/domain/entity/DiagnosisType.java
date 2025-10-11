package com.pinHouse.server.platform.diagnostic.explanation.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.pinHouse.server.core.response.response.ErrorCode;
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

    @JsonCreator
    public static DiagnosisType fromDescription(String description) {
        for (DiagnosisType diagnosisType : DiagnosisType.values()) {
            if (diagnosisType.getDescription().equals(description)) {
                return diagnosisType;
            }
        }
        throw new IllegalArgumentException(ErrorCode.INVALID_INPUT_ENUM.getMessage());
    }

}

