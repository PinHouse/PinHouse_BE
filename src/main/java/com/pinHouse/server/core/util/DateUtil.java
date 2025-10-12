package com.pinHouse.server.core.util;

import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

    public static String formatDate(String startDate, String endDate) {

        /// 둘 다 없다면
        if (startDate == null || endDate == null) {
            return "공고 확인 바람";
        }

        /// 시작일만 있다면
        if (startDate != null || endDate == null) {
            return startDate + "~";
        }

        return startDate + "~" + endDate;

    }
}
