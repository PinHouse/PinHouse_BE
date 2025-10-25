package com.pinHouse.server.core.exception.code;

import com.pinHouse.server.core.response.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum PinPointErrorCode implements ErrorCode {

    // ========================
    // 400 Bad Request
    // ========================
    BAD_REQUEST_PINPOINT(400_005, HttpStatus.BAD_REQUEST, "핀포인트와 유저가 일치하지않습니다."),

    // ========================
    // 401 Unauthorized
    // ========================

    // ========================
    // 403 Forbidden
    // ========================
    FORBIDDEN_DELETE(403_003, HttpStatus.FORBIDDEN, "해당 핀포인트를 삭제할 권한이 없습니다."),

    // ========================
    // 404 Not Found
    // ========================
    NOT_FOUND_PINPOINT(404_009, HttpStatus.NOT_FOUND, "해당 핀포인트를 찾을 수 없습니다"),

    // ========================
    // 500 Server Error
    // ========================
    KAKAO_SERVER_ERROR(500_000, HttpStatus.INTERNAL_SERVER_ERROR, "Kakao API 호출 실패");

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
