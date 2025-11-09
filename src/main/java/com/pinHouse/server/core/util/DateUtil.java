package com.pinHouse.server.core.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtil {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.KOREAN);

    /**
     * 시작일과 종료일을 yyyy년 MM월 dd일 포맷으로 반환합니다.
     * - 둘 다 null → "공고 확인 바람"
     * - 시작일만 있음 → "yyyy년 MM월 dd일 ~"
     * - 둘 다 있음 → "yyyy년 MM월 dd일 ~ yyyy년 MM월 dd일"
     */
    public static String formatDate(LocalDate startDate, LocalDate endDate) {

        if (startDate == null && endDate == null) {
            return "공고 확인 바람";
        }

        if (startDate != null && endDate == null) {
            return startDate.format(FORMATTER) + " ~";
        }

        if (startDate == null && endDate != null) {
            return "~ " + endDate.format(FORMATTER);
        }

        return startDate.format(FORMATTER) + " ~ " + endDate.format(FORMATTER);
    }
}
