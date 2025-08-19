package com.pinHouse.server.platform.domain.diagnosis.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MaritalStatus {

    SINGLE("미혼"),
    MARRIED("기혼"),
    DIVORCED("이혼"),
    WIDOWED("사별");

    private final String value;

    @JsonCreator
    public static MaritalStatus fromString(String value) {
        for (MaritalStatus maritalStatus : MaritalStatus.values()) {
            // 한글 값 or Enum name 모두 허용
            if (maritalStatus.value.equals(value) || maritalStatus.name().equalsIgnoreCase(value)) {
                return maritalStatus;
            }
        }
        throw new IllegalStateException("Invalid MaritalStatus value: " + value);
    }

    @JsonValue
    public String getValue() {
        return value;

    }
}
