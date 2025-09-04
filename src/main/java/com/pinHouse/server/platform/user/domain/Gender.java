package com.pinHouse.server.platform.user.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Gender {

    @JsonProperty("남성")
    Male("M"),

    @JsonProperty("여성")
    Female("F"),

    @JsonProperty("기타")
    Other("U");

    private final String value;

    /**
     * value로 값 얻기
     * @param value 밸류
     */
    public static Gender getGender(String value) {
        for (Gender gender : Gender.values()) {
            if (gender.value.equals(value)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 성별 코드입니다: " + value);
    }


}
