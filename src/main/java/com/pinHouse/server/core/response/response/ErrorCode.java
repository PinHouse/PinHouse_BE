package com.pinHouse.server.core.response.response;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    Integer getCode();

    String getMessage();

    HttpStatus getHttpStatus();

}
