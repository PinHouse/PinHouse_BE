package com.pinHouse.server.platform.housing.complex.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 열차 타입
 * 외부 API의 trainType 값을 타입 안전하게 매핑
 */
@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TrainType {
    KTX(1, "KTX", "#3356B4"),
    SAEMAUL(2, "새마을", "#00A84D"),
    MUGUNHWA(3, "무궁화", "#EB6100"),
    NURIRO(4, "누리로", "#09B5EA"),
    COMMUTER(5, "통근", "#924BDD"),
    ITX(6, "ITX", "#B55E16"),
    ITX_CHEONGCHUN(7, "ITX-청춘", "#69702D"),
    SRT(8, "SRT", "#E5046C"),

    UNKNOWN(-1, "UNKNOWN", "#BBBBBB");

    private final int code;
    private final String label;

    @JsonIgnore
    private final String colorHex;

    /**
     * 숫자 코드로부터 TrainType을 조회
     * @param code 열차 타입 코드
     * @return 매칭되는 TrainType, 없으면 UNKNOWN
     */
    public static TrainType from(int code) {
        for (TrainType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
