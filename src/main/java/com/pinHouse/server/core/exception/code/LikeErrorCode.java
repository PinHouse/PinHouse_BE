package com.pinHouse.server.core.exception.code;

import com.pinHouse.server.core.response.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum LikeErrorCode implements ErrorCode {

    // ========================
    // 400 Bad Request
    // ========================
    BAD_REQUEST_LIKE(400_000, HttpStatus.BAD_REQUEST, "좋아요 파라미터가 잘못되었습니다."),

    // ========================
    // 401 Unauthorized
    // ========================

    // ========================
    // 403 Forbidden
    // ========================

    // ========================
    // 404 Not Found
    // ========================
    NOT_FOUND_LIKE(404_000, HttpStatus.NOT_FOUND, "취소할 좋아요가 없습니다."),


    // ========================
    // 409 Conflict
    // ========================
    DUPLICATE_LIKE(409_000, HttpStatus.CONFLICT, "이미 좋아요를 눌렀습니다.");

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
