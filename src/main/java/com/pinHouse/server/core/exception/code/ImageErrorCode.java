package com.pinHouse.server.core.exception.code;

import com.pinHouse.server.core.response.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 이미지 관련 예외처리 클래스입니다.
 */
@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements ErrorCode {

    // ========================
    // 400 Bad Request
    // ========================
    INVALID_FILE_TYPE(400_301, HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다. (jpg, jpeg, png, gif만 가능)"),
    INVALID_FILE_EXTENSION(400_302, HttpStatus.BAD_REQUEST, "파일 확장자가 유효하지 않습니다."),
    FILE_SIZE_EXCEEDED(400_303, HttpStatus.BAD_REQUEST, "파일 크기가 5MB를 초과합니다."),
    INVALID_FILE_NAME(400_304, HttpStatus.BAD_REQUEST, "파일명이 유효하지 않습니다."),

    // ========================
    // 500 Internal Server Error
    // ========================
    S3_PRESIGNED_URL_GENERATION_FAILED(500_301, HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드 URL 생성에 실패했습니다."),
    S3_CLIENT_ERROR(500_302, HttpStatus.INTERNAL_SERVER_ERROR, "S3 서버와 통신 중 오류가 발생했습니다.");

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
