package com.pinHouse.server.platform.housing.complex.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 고속/시외버스 좌석 등급 타입
 * 외부 API의 고속/시외버스 type 값을 타입 안전하게 매핑
 */
@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ExpressBusType {
    GENERAL(1, "일반", "#0069B3"),
    PREMIUM(2, "우등", "#2E933C"),
    SUPER_PREMIUM(3, "프리미엄", "#F68A1E"),
    NIGHT_GENERAL(4, "심야 일반", "#1B1C3A"),
    NIGHT_PREMIUM(5, "심야 우등", "#1B1C3A"),
    NIGHT_SUPER_PREMIUM(6, "심야 프리미엄", "#1B1C3A"),
    WEEKEND_PREMIUM(7, "주말 프리미엄", "#BBBBBB"),
    WEEKEND_NIGHT_PREMIUM(8, "주말심야 프리미엄", "#1B1C3A"),

    UNKNOWN(-1, "UNKNOWN", "#BBBBBB");

    private final int code;
    private final String label;

    @JsonIgnore
    private final String colorHex;

    /**
     * 숫자 코드로부터 ExpressBusType을 조회
     * @param code 고속/시외버스 좌석 등급 코드
     * @return 매칭되는 ExpressBusType, 없으면 UNKNOWN
     */
    public static ExpressBusType from(int code) {
        for (ExpressBusType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
