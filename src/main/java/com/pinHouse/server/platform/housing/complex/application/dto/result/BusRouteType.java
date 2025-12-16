package com.pinHouse.server.platform.housing.complex.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 버스 노선 타입
 * 외부 API의 버스 type 값을 타입 안전하게 매핑
 */
@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BusRouteType {
    GENERAL(1, "일반", "#0069B3"),
    SEAT(2, "좌석", "#2E933C"),
    VILLAGE(3, "마을버스", "#86C34B"),
    DIRECT_SEAT(4, "직행좌석", "#D82628"),
    AIRPORT(5, "공항버스", "#F68A1E"),

    TRUNK(6, "간선급행", "#E6002D"),
    OUTER(10, "외곽", "#8E8E8E"),
    TRUNK_LINE(11, "간선", "#0069B3"),
    BRANCH(12, "지선", "#2E933C"),
    CIRCULAR(13, "순환", "#F8B600"),
    WIDE_AREA(14, "광역", "#D82628"),
    EXPRESS(15, "급행", "#F8B600"),

    TOUR(16, "관광버스", "#6A4FB3"),
    RURAL(20, "농어촌버스", "#5B8C3B"),
    GYEONGGI_INTERCITY(22, "경기도 시외형버스", "#D82628"),
    EXPRESS_TRUNK(26, "급행간선", "#C8102E"),
    HAN_RIVER(30, "한강버스", "#1CA9C9"),

    UNKNOWN(-1, "UNKNOWN", "#BBBBBB");


    private final int code;
    private final String label;

    @JsonIgnore
    private final String colorHex;

    /**
     * 숫자 코드로부터 BusRouteType을 조회
     * @param code 버스 타입 코드
     * @return 매칭되는 BusRouteType, 없으면 UNKNOWN
     */
    public static BusRouteType from(int code) {
        for (BusRouteType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return UNKNOWN;
    }

    /**
     * 문자열 코드로부터 BusRouteType을 조회
     * @param codeStr 버스 타입 코드 문자열
     * @return 매칭되는 BusRouteType, 파싱 실패 시 UNKNOWN
     */
    public static BusRouteType from(String codeStr) {
        if (codeStr == null || codeStr.isBlank()) {
            return UNKNOWN;
        }
        try {
            int code = Integer.parseInt(codeStr.trim());
            return from(code);
        } catch (NumberFormatException e) {
            return UNKNOWN;
        }
    }

    /**
     * BusRouteType을 LineInfo로 변환
     * @return LineInfo 객체
     */
    public LineInfo toLineInfo() {
        return LineInfo.builder()
                .code(this.code)
                .label(this.label)
                .bgColorHex(this.colorHex)
                .build();
    }
}
