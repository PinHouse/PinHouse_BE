package com.myhome.server.core.response.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@RequiredArgsConstructor
public class ExceptionDto {
    @NotNull
    private final Integer code;

    @NotNull
    private final String message;

    @NotNull
    private final String errorMessage;

    public ExceptionDto(ErrorCode errorCode, String errorMessage) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.errorMessage = errorMessage;
    }

    public static ExceptionDto of(ErrorCode errorCode, String errorMessage) {
        return new ExceptionDto(errorCode, errorMessage);
    }

    public static ExceptionDto of(ErrorCode errorCode) {
        return new ExceptionDto(errorCode, null);
    }

}
