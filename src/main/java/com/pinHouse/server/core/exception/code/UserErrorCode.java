package com.pinHouse.server.core.exception.code;

import com.pinHouse.server.core.response.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 유저 예외처리 클래스입니다.
 */
@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    // ========================
    // 400 Bad Request
    // ========================
    BAD_REQUEST_ONBOARDING(400_201,HttpStatus.BAD_REQUEST,"회원가입 온보딩 중에 문제가 발생했습니다."),
    BAD_REQUEST_REDIS(400_202, HttpStatus.BAD_REQUEST, "지원하지 않는 Redis 값 타입입니다."),
    BAD_REQUEST_BIRTHDAY(400_203, HttpStatus.BAD_REQUEST, "생년월일이 null일 수 없습니다."),
    BAD_REQUEST_ROLE(400_204, HttpStatus.BAD_REQUEST, "유저 역할 파라미터에 오류가 있습니다."),

    // ========================
    // 401 Unauthorized
    // ========================

    // ========================
    // 403 Forbidden
    // ========================

    // ========================
    // 404 Not Found
    // ========================
    NOT_TEMP_USER_KEY(404_000, HttpStatus.NOT_FOUND, "해당 임시 키를 가진 유저를 찾을 수 없습니다."),
    NOT_FOUND_USER(404_001, HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),

    // ========================
    // 409 Conflict
    // ========================
    CONFLICT_DUPLICATE_NICKNAME(409_000, HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    CONFLICT_DUPLICATE_EMAIL(409_001, HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");

    // ========================
    // 500 Internal Server Error
    // ========================


    /**
     * 에러 코드 (고유값)
     */
    private final Integer code;

    /**
     * HTTP 상태 코드
     */
    private final HttpStatus httpStatus;

    /**
     * 에러 메시지
     */
    private final String message;

}
