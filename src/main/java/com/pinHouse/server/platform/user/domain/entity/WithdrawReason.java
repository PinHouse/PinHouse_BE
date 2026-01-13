package com.pinHouse.server.platform.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum WithdrawReason {

    POOR_RECOMMENDATION("추천 결과가 내 조건과 잘 맞지 않아요"),
    INSUFFICIENT_NOTICES("원하는 공고/단지가 부족해요"),
    DIFFICULT_FILTER("필터/검색이 불편하거나 원하는 조건을 찾기 어려워요"),
    APP_ERROR_OR_SLOW("앱 사용 중 오류가 있거나 속도가 느려요"),
    TOO_MANY_NOTIFICATIONS("필요 없는 안내가 많아요");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * value로 enum 찾기
     * @param value 밸류
     */
    @JsonCreator
    public static WithdrawReason fromValue(String value) {
        for (WithdrawReason reason : WithdrawReason.values()) {
            if (reason.value.equals(value)) {
                return reason;
            }
        }
        throw new IllegalArgumentException("Unknown withdraw reason: " + value);
    }
}
