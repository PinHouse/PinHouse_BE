package com.pinHouse.server.platform.housing.complex.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 지하철 노선 타입
 * 외부 API의 subwayCode 값을 타입 안전하게 매핑
 */
@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SubwayLineType {
    // 수도권
    SEOUL_LINE_1(1, "수도권 1호선", "#3356B4"),
    SEOUL_LINE_2(2, "수도권 2호선", "#00A84D"),
    SEOUL_LINE_3(3, "수도권 3호선", "#EB6100"),
    SEOUL_LINE_4(4, "수도권 4호선", "#09B5EA"),
    SEOUL_LINE_5(5, "수도권 5호선", "#924BDD"),
    SEOUL_LINE_6(6, "수도권 6호선", "#B55E16"),
    SEOUL_LINE_7(7, "수도권 7호선", "#69702D"),
    SEOUL_LINE_8(8, "수도권 8호선", "#E5046C"),
    SEOUL_LINE_9(9, "수도권 9호선", "#CEA43A"),
    GTX_A(91, "GTX-A", "#9A6292"),
    AIRPORT(101, "공항철도", "#3CA8EA"),
    MAGLEV(102, "자기부상철도", "#BBBBBB"),
    GYEONGUI_JUNGANG(104, "경의중앙선", "#69CCCC"),
    EVERLINE(107, "에버라인", "#6CAB2A"),
    GYEONGCHUN(108, "경춘선", "#BBBBBB"),
    SINBUNDANG(109, "신분당선", "#C12B2F"),
    UIJEONGBU(110, "의정부경전철", "#DF7503"),
    GYEONGGANG(112, "경강선", "#BBBBBB"),
    UISINSEOL(113, "우이신설선", "#AFBF04"),
    SEOHAE(114, "서해선", "#4DA635"),
    GIMPO_GOLDLINE(115, "김포골드라인", "#957326"),
    SUIN_BUNDANG(116, "수인분당선", "#FFCE32"),
    SILLIM(117, "신림선", "#BBBBBB"),

    // 인천
    INCHEON_LINE_1(21, "인천 1호선", "#7EAAD6"),
    INCHEON_LINE_2(22, "인천 2호선", "#BBBBBB"),

    // 대전
    DAEJEON_LINE_1(31, "대전 1호선", "#BBBBBB"),

    // 대구
    DAEGU_LINE_1(41, "대구 1호선", "#BBBBBB"),
    DAEGU_LINE_2(42, "대구 2호선", "#BBBBBB"),
    DAEGU_LINE_3(43, "대구 3호선", "#BBBBBB"),
    DAEGYEONG(48, "대경선", "#BBBBBB"),

    // 광주
    GWANGJU_LINE_1(51, "광주 1호선", "#BBBBBB"),

    // 부산
    BUSAN_LINE_1(71, "부산 1호선", "#BBBBBB"),
    BUSAN_LINE_2(72, "부산 2호선", "#BBBBBB"),
    BUSAN_LINE_3(73, "부산 3호선", "#BBBBBB"),
    BUSAN_LINE_4(74, "부산 4호선", "#BBBBBB"),
    DONGHAE(78, "동해선", "#BBBBBB"),
    BUSAN_GIMHAE(79, "부산-김해경전철", "#BBBBBB"),

    UNKNOWN(-1, "UNKNOWN", "#BBBBBB");

    private final int code;
    private final String name;
    @JsonIgnore
    private final String colorHex;

    /**
     * 숫자 코드로부터 SubwayLineType을 조회
     * @param code 지하철 노선 코드
     * @return 매칭되는 SubwayLineType, 없으면 UNKNOWN
     */
    public static SubwayLineType from(int code) {
        for (SubwayLineType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return UNKNOWN;
    }

    /**
     * 문자열 코드로부터 SubwayLineType을 조회
     * @param codeStr 지하철 노선 코드 문자열
     * @return 매칭되는 SubwayLineType, 파싱 실패 시 UNKNOWN
     */
    public static SubwayLineType from(String codeStr) {
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
}
