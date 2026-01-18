package com.pinHouse.server.platform.home.application.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.pinHouse.server.core.exception.code.CommonErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HomeSearchCategoryType {
    NOTICE("NOTICE"),
    COMPLEX("COMPLEX"),
    TARGET_GROUP("TARGET_GROUP"),
    REGION("REGION"),
    HOUSE_TYPE("HOUSE_TYPE");

    private final String value;

    @JsonCreator
    public static HomeSearchCategoryType from(String value) {
        for (HomeSearchCategoryType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new CustomException(CommonErrorCode.BAD_PARAMETER);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
