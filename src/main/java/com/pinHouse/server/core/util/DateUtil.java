package com.pinHouse.server.core.util;

import java.time.format.DateTimeFormatter;

import java.time.*;
import java.util.Date;

public class DateUtil {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

    // =================
    //  퍼블릭 로직
    // =================

    public static String formatDate(Date startDate, Date endDate) {
        LocalDate s = toLocalDate(startDate);
        LocalDate e = toLocalDate(endDate);
        return formatRange(s, e);
    }


    // =================
    //  내부 로직
    // =================
    private static String formatRange(LocalDate s, LocalDate e) {
        // 둘 다 null
        if (s == null && e == null) return "공고 확인 바람";

        // 둘 중 하나만 존재
        if (s != null && e == null) return s.format(FORMATTER) + " ~";
        if (s == null)             return "~ " + e.format(FORMATTER);

        // 둘 다 존재: 역전되면 교환
        if (e.isBefore(s)) {
            LocalDate tmp = s; s = e; e = tmp;
        }

        // 같은 날이면 한 번만
        if (s.isEqual(e)) return s.format(FORMATTER);

        // 범위 표기
        return s.format(FORMATTER) + " ~ " + e.format(FORMATTER);
    }

    private static LocalDate toLocalDate(Date d) {
        return (d == null) ? null : d.toInstant().atZone(KST).toLocalDate();
    }

}
