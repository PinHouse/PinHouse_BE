package com.pinHouse.server.platform.search.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.pinHouse.server.core.exception.code.CommonErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import lombok.RequiredArgsConstructor;

/// 조건
@RequiredArgsConstructor
public enum RentalType {

    /// 청년층
    YOUTH_SPECIAL("청년"),
    STUDENT_SPECIAL("대학생"),

    /// 가족형
    COUPLE_SPECIAL("신혼부부"),
    MULTI_CHILD_SPECIAL("다자녀"),

    /// 주거약자
    ELDER_SPECIAL("고령자"),
    DISABLED_SPECIAL("장애인"),
    SINGLE_PARENT_SPECIAL("한부모"),
    VETERAN_SPECIAL("국가유공자"),
    LOW_INCOME_SPECIAL("저소득층"),

    /// 주택보유상태
    NO_OWN_SPECIAL("무주택자"),
    OWN_SPECIAL("유주택자");

    private final String value;

    /// 한글값
    @JsonValue
    public String getValue() {
        return value;
    }


    /// 생성기
    @JsonCreator
    public static RentalType fromValue(String value) {
        for (RentalType type : RentalType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new CustomException(CommonErrorCode.BAD_PARAMETER);
    }
}
