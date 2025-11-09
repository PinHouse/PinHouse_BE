package com.pinHouse.server.platform.search.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.pinHouse.server.core.exception.code.CommonErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum HouseType {

    APARTMENT("아파트"),
    OFFICE("오피스텔"),
    DORMITORY("기숙사"),
    MULTI_FAMILY("다세대주택"),
    ROW_HOUSE("연립주택"),
    DETACHED_HOUSE("단독주택");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static HouseType fromValue(String value) {
        for (HouseType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new CustomException(CommonErrorCode.BAD_PARAMETER);
    }
}
