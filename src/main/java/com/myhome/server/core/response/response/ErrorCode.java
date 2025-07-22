package com.myhome.server.core.response.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Test Error
    TEST_ERROR(100, HttpStatus.BAD_REQUEST, "테스트 에러입니다."),
    // 400 Bad Request
    BAD_REQUEST(400, HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    // 401 SC_UNAUTHORIZED
    SC_UNAUTHORIZED(40100, HttpStatus.UNAUTHORIZED, "로그인이 필요합니다"),
    // 403 Bad Reques
    FORBIDDEN(40300, HttpStatus.FORBIDDEN, "접속 권한이 없습니다."),
    // 404 Not Found
    NOT_FOUND_END_POINT(404, HttpStatus.NOT_FOUND, "요청한 대상이 존재하지 않습니다."),
    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),

    /// 유저 관련
    NOT_USER(1000, HttpStatus.NOT_FOUND, "해당하는 유저가 존재하지 않습니다."),
    NOT_CONSENT(1010, HttpStatus.BAD_REQUEST, "유저 정보 동의가 체크되지 않았습니다"),
    FIRST_LOGIN(1200, HttpStatus.NOT_FOUND, "최초 로그인유저이기에 추가정보기입이 필요합니다."),

    /// 공고 관련
    NOT_NOTICE(2000, HttpStatus.NOT_FOUND, "해당하는 공고가 존재하지 않습니다"),


    /// 파라미터 관련
    BAD_PARAMETER(10000, HttpStatus.BAD_REQUEST, "요청 파라미터에 문제가 존재합니다."),
    ;

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;


    /// 에러코드로 변환시키기
    public static ErrorCode fromMessage(String message) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getMessage().equals(message)) {
                return errorCode;
            }
        }
        throw new IllegalArgumentException("해당 message를 가진 ErrorCode가 존재하지 않습니다: " + message);
    }
}
