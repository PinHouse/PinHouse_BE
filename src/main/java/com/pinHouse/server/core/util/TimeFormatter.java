package com.pinHouse.server.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 시간 포맷팅 유틸리티 클래스
 * 분(minutes) 단위 시간을 "X시간 Y분" 형식의 문자열로 변환합니다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimeFormatter {

    /**
     * 시간을 "X시간 Y분" 형식으로 포맷팅
     *
     * @param totalMinutes 총 시간(분)
     * @return 포맷팅된 시간 문자열 (예: "1시간 30분", "45분")
     *         - 0 이하면 "0분" 반환
     *         - 60분 미만이면 "X분" 형식
     *         - 60분 이상이면 "X시간" 또는 "X시간 Y분" 형식
     */
    public static String formatTime(int totalMinutes) {
        if (totalMinutes <= 0) {
            return "0분";
        }

        if (totalMinutes < 60) {
            return totalMinutes + "분";
        }

        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        if (minutes == 0) {
            return hours + "시간";
        }

        return hours + "시간 " + minutes + "분";
    }

    /**
     * 시간을 "X시간 Y분" 형식으로 포맷팅 (null 반환 버전)
     *
     * @param totalMinutes 총 시간(분)
     * @return 포맷팅된 시간 문자열 (예: "1시간 30분", "45분")
     *         - 0 이하면 null 반환
     *         - 나머지는 formatTime()과 동일
     */
    public static String formatTimeOrNull(int totalMinutes) {
        if (totalMinutes <= 0) {
            return null;
        }

        return formatTime(totalMinutes);
    }
}
