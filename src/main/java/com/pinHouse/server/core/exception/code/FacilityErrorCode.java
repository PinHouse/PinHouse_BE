package com.pinHouse.server.core.exception.code;

import com.pinHouse.server.core.response.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum FacilityErrorCode implements ErrorCode {

    // ========================
    // 400 Bad Request
    // ========================
    BAD_REQUEST_FACILITY(400_000, HttpStatus.BAD_REQUEST, "시설에 해당하지 않는 타입입니다"),
    BAD_REQUEST_INPUT_FACILITY(400_001, HttpStatus.BAD_REQUEST, "시설에 해당하지 않는 입력 파라미터입니다");

    // ========================
    // 401 Unauthorized
    // ========================

    // ========================
    // 403 Forbidden
    // ========================

    // ========================
    // 404 Not Found
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
