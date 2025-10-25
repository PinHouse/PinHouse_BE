package com.pinHouse.server.core.exception.code;

import com.pinHouse.server.core.response.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ComplexErrorCode implements ErrorCode {

    // ========================
    // 400 Bad Request
    // ========================
    BAD_REQUEST_DISTANCE(400_000, HttpStatus.BAD_REQUEST, "입력 좌표에서 인코딩 문제가 생겼습니다"),

    // ========================
    // 401 Unauthorized
    // ========================

    // ========================
    // 403 Forbidden
    // ========================

    // ========================
    // 404 Not Found
    // ========================
    NOT_FOUND_COMPLEX(404_009, HttpStatus.NOT_FOUND, "해당 임대주택을 찾을 수 없습니다"),

    // ========================
    // 500 Server Error
    // ========================
    ODSAY_SERVER_ERROR(500_001, HttpStatus.INTERNAL_SERVER_ERROR, "ODsay API 호출 실패"),
    ODSAY_PARSING_ERROR(500_002, HttpStatus.INTERNAL_SERVER_ERROR, "ODsay API 응답 처리 실패");


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
