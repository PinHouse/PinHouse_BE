package com.pinHouse.server.core.util;

import com.pinHouse.server.core.exception.code.UserErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Component
public class BirthDayUtil {

    public static String formatString(LocalDate birthday) {
        if (birthday == null) {
            return "정보 없음";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        return birthday.format(formatter);
    }

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
            return null;
        }
    }

    /**
     * 생년월일로 나이 계산 (만 나이)
     * @param birthday LocalDate 형태의 생년월일
     */
    public static int calculateAge(LocalDate birthday) {
        if (birthday == null) {
            throw new CustomException(UserErrorCode.BAD_REQUEST_BIRTHDAY);
        }

        LocalDate today = LocalDate.now();
        return Period.between(birthday, today).getYears();
    }



}
