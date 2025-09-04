package com.pinHouse.server.core.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class BirthDayUtil {

    /**
     * 생년월일 합치는 유틸 클래스
     * @param year      년도
     * @param monthDay  월일
     */
    public static LocalDate parseBirthday(String year, String monthDay) {
        try {
            String fullDate = year + "-" + monthDay;
            return LocalDate.parse(fullDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 생년월일 형식입니다. year=" + year + ", monthDay=" + monthDay);
        }
    }

    /**
     * 생년월일로 나이 계산 (만 나이)
     * @param birthday LocalDate 형태의 생년월일
     */
    public static int calculateAge(LocalDate birthday) {
        if (birthday == null) {
            throw new IllegalArgumentException("생년월일이 null일 수 없습니다.");
        }
        LocalDate today = LocalDate.now();
        return Period.between(birthday, today).getYears();
    }



}
