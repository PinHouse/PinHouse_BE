package com.pinHouse.server.core.exception;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.core.response.response.FieldErrorResponse;
import com.pinHouse.server.security.jwt.application.exception.JwtAuthenticationException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 전역 예외 처리 핸들러
 * - @Hidden 은 스웨거에서 인식 되지 않도록 에러 수정용
 */

@Hidden
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /// 공통 처리 메서드
    private ApiResponse<CustomException> handleCustomException(CustomException customException) {

        log.error(customException.getMessage());

        return ApiResponse.fail(customException);
    }


    /// 예외 처리
    // 최하위 예외처리
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse<CustomException> handleException(Exception e, HttpServletRequest request) {

        /// 로그 발생
        log.error(e.getMessage(), e);

        /// 500 예외 코드 검색
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        /// 해당 예외 코드로 예외 처리
        CustomException exception = new CustomException(errorCode, null);

        return handleCustomException(exception, request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            IllegalStateException.class, IllegalArgumentException.class})
    public ApiResponse<CustomException> handleIllegalStateException(Exception e, HttpServletRequest request) {


        log.error(e.getMessage(), e);

        /// 메세지 바탕으로 예외 코드 검색
        ErrorCode errorCode = ErrorCode.fromMessage(e.getMessage());

        /// 해당 예외 코드로 예외 처리
        CustomException exception = new CustomException(errorCode, null);

        return handleCustomException(exception, request);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({JwtAuthenticationException.class})
    public ApiResponse<CustomException> handleJwtAuthenticationException(JwtAuthenticationException e, HttpServletRequest request) {

        log.error(e.getMessage(), e);


        /// 메세지 바탕으로 예외 코드 검색
        ErrorCode errorCode = ErrorCode.fromMessage(e.getMessage());

        /// 해당 예외 코드로 예외 처리
        CustomException exception = new CustomException(errorCode, null);

        return handleCustomException(exception, request);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoSuchElementException.class, NoResourceFoundException.class})
    public ApiResponse<CustomException> handleNoSuchException(Exception e) {

        log.error(e.getMessage(), e);


        /// 메세지 바탕으로 예외 코드 검색
        ErrorCode errorCode = ErrorCode.fromMessage(e.getMessage());

        /// 해당 예외 코드로 예외 처리
        CustomException exception = new CustomException(errorCode, null);

        return handleCustomException(exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<CustomException> handleValidationExceptions(MethodArgumentNotValidException e, HttpServletRequest request) {

        log.error(e.getMessage(), e);


        /// 파라미터용 예외 코드
        ErrorCode errorCode = ErrorCode.BAD_PARAMETER;

        /// BindingResult 바탕으로 필드에러 List 생성
        List<FieldErrorResponse> errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> FieldErrorResponse.of(error.getField(), error.getDefaultMessage()))
                .toList();

        CustomException exception = new CustomException(errorCode, errors);

        return handleCustomException(exception);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public ApiResponse<CustomException> handleValidationExceptions(NoResourceFoundException e) {

        log.error(e.getMessage(), e);

        /// 파라미터용 예외 코드
        ErrorCode errorCode = ErrorCode.NOT_FOUND;

        CustomException exception = new CustomException(errorCode, null);

        return handleCustomException(exception);
    }



}
