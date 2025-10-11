package com.pinHouse.server.platform.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Gender {

    Male("남성"),

    Female("여성"),

    Other("미정");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * value로 값 얻기
     * @param value 밸류
     */
    @JsonCreator
    public static Gender getGender(String value) {
        for (Gender gender : Gender.values()) {
            if (gender.value.equals(value)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 성별 코드입니다: " + value);
    }


}
