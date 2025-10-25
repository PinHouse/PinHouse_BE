package com.pinHouse.server.core.exception.code;

import com.pinHouse.server.core.response.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 보안 예외처리 클래스입니다.
 */
@Getter
@RequiredArgsConstructor
public enum SecurityErrorCode implements ErrorCode {

    // ========================
    // 400 Bad Request
    // ========================
    BAD_REQUEST_LOGIN(400_100, HttpStatus.BAD_REQUEST, "로그인할 수 없습니다."),
    BAD_REQUEST_OAUTH2(400_101, HttpStatus.BAD_REQUEST, "OAUTH2 파라미터가 잘못되었습니다."),

    // ========================
    // 401 Unauthorized
    // ========================
    ACCESS_TOKEN_EXPIRED(401_101, HttpStatus.UNAUTHORIZED, "액세스 토큰이 만료되었습니다."),
    ACCESS_TOKEN_INVALID(401_102, HttpStatus.UNAUTHORIZED, "액세스 토큰이 유효하지 않습니다."),
    ACCESS_TOKEN_SIGNATURE(401_103, HttpStatus.UNAUTHORIZED, "액세스 토큰의 서명이 유효하지 않습니다."),
    ACCESS_TOKEN_UNSUPPORTED(401_104, HttpStatus.UNAUTHORIZED, "액세스 토큰이 지원하지 않는 형식입니다."),
    ACCESS_TOKEN_MALFORMED(401_105, HttpStatus.UNAUTHORIZED, "액세스 토큰의 구조가 깨진 형식입니다."),
    ACCESS_TOKEN_NOT_FOUND(401_106, HttpStatus.UNAUTHORIZED, "액세스 토큰이 존재하지 않습니다."),
    ACCESS_TOKEN_NOT_USER(401_107, HttpStatus.UNAUTHORIZED, "액세스 토큰에 해당하는 유저가 존재하지 않습니다."),

    REFRESH_TOKEN_EXPIRED(401_108, HttpStatus.UNAUTHORIZED, "리프레쉬 토큰이 만료되었습니다."),
    REFRESH_TOKEN_INVALID(401_109, HttpStatus.UNAUTHORIZED, "리프레쉬 토큰이 유효하지 않은 토큰입니다."),
    REFRESH_TOKEN_UNSUPPORTED(401_110, HttpStatus.UNAUTHORIZED, "리프레쉬 토큰이 지원하지 않는 토큰 형식입니다."),
    REFRESH_TOKEN_NOT_USER(401_111, HttpStatus.UNAUTHORIZED, "리프레쉬 토큰에 해당하는 유저가 존재하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(401_112, HttpStatus.UNAUTHORIZED, "리프레쉬 토큰이 없기에 재로그인이 필요합니다."),
    REFRESH_TOKEN_LOGOUT(401_113, HttpStatus.UNAUTHORIZED, "리프레쉬 토큰을 사용한 로그아웃에 유저가 다릅니다."),


    // ========================
    // 403 Forbidden
    // ========================
    FORBIDDEN(403_100, HttpStatus.FORBIDDEN, "접속 권한이 없습니다."),
    ACCESS_DENY(403_101, HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),
    UNAUTHORIZED_POST_ACCESS(403_102, HttpStatus.FORBIDDEN, "해당 게시글에 접근할 권한이 없습니다."),

    // ========================
    // 404 Not Found
    // ========================
    NOT_FOUND_EMAIL(404_100, HttpStatus.NOT_FOUND, "해당 이메일을 가진 유저가 없습니다"),
    NOT_FOUND_ID(404_101, HttpStatus.NOT_FOUND, "해당 아이디을 가진 유저가 없습니다"),
    USER_NOT_FOUND_IN_COOKIE(404_102, HttpStatus.NOT_FOUND, "쿠키에서 사용자 정보를 찾을 수 없습니다.");

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
