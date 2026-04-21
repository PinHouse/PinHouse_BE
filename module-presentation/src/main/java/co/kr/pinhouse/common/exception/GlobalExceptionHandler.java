package co.kr.pinhouse.common.exception;

import static co.kr.pinhouse.common.util.LogSanitizer.sanitize;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.mongodb.MongoCommandException;

import co.kr.pinhouse.common.exception.code.CommonErrorCode;
import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.common.response.CustomException;
import co.kr.pinhouse.common.response.ErrorCode;
import co.kr.pinhouse.common.response.FieldErrorResponse;
import jakarta.validation.UnexpectedTypeException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/// CustomException 에러 처리
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException exception) {

		/// 에러 코드
		ErrorCode errorCode = exception.getErrorCode();

		/// 로그찍기
		log.error(toLogMessage(errorCode.getMessage()));

		/// 응답
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ApiResponse.fail(exception));
	}

	// JWT 관련 에러 처리는 security 모듈로 이동됨
	// JWT exception handler will be in security module

	/// @Valid 파라미터 에러 처리
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ApiResponse<CustomException> handleValidationExceptions(MethodArgumentNotValidException exception) {

		/// 에러 이유 로그 찍기
		log.error(toLogMessage(exception.getMessage()));

		/// 파라미터용 예외 코드
		ErrorCode errorCode = CommonErrorCode.BAD_PARAMETER;

		/// 기본 에러 코드로 응답 생성 및 파라미터 담기
		List<FieldErrorResponse> errors = exception.getBindingResult().getFieldErrors()
			.stream()
			.map(error -> FieldErrorResponse.of(error.getField(), error.getDefaultMessage()))
			.toList();

		CustomException customException = new CustomException(errorCode, errors);

		/// 응답
		return ApiResponse.fail(customException);
	}

	/// 레디스 에러 처리 핸들러
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	@ExceptionHandler(RedisConnectionFailureException.class)
	public ApiResponse<?> handleRedisConnectionFailureException(RedisConnectionFailureException exception) {

		/// 에러 이유 로그 찍기
		log.error(toLogMessage(exception.getMessage()));

		/// 기본 에러 코드로 응답 생성
		ErrorCode errorCode = CommonErrorCode.INTERNAL_REDIS_SERVER_ERROR;
		CustomException customException = new CustomException(errorCode);

		/// 응답
		return ApiResponse.fail(customException);
	}

	/// 몽고디비 에러 처리 핸들러
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	@ExceptionHandler({UncategorizedMongoDbException.class, MongoCommandException.class})
	public ApiResponse<?> handleMongoException(UncategorizedMongoDbException exception) {

		/// 에러 이유 로그 찍기
		log.error(toLogMessage(exception.getMessage()));

		/// 기본 에러 코드로 응답 생성
		ErrorCode errorCode = CommonErrorCode.INTERNAL_MONGO_SERVER_ERROR;
		CustomException customException = new CustomException(errorCode);

		/// 응답
		return ApiResponse.fail(customException);
	}

	/// 값이 없는 내용 에러 처리
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler({NoSuchElementException.class, NoResourceFoundException.class})
	public ApiResponse<?> handleNoSuchException(Exception exception) {

		/// 에러 이유 로그 찍기
		log.error(toLogMessage(exception.getMessage()));

		/// 기본 에러 코드로 응답 생성
		ErrorCode errorCode = CommonErrorCode.NOT_FOUND;
		CustomException customException = new CustomException(errorCode);

		/// 응답
		return ApiResponse.fail(customException);
	}

	/// 값이 없는 내용 에러 처리
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NullPointerException.class)
	public ApiResponse<?> handleNullPointerException(NullPointerException exception) {

		/// 에러 이유 로그 찍기
		log.error(toLogMessage(exception.getMessage()));

		/// 기본 에러 코드로 응답 생성
		ErrorCode errorCode = CommonErrorCode.NULL_VALUE;
		CustomException customException = new CustomException(errorCode);

		/// 응답
		return ApiResponse.fail(customException);
	}

	/// 값이 없는 내용 에러 처리
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
	public ApiResponse<?> handleIllegalException(Exception exception) {

		/// 에러 이유 로그 찍기
		log.error(toLogMessage(exception.getMessage()), exception);

		/// 기본 에러 코드로 응답 생성
		ErrorCode errorCode = CommonErrorCode.BAD_REQUEST;
		CustomException customException = new CustomException(errorCode);

		/// 응답
		return ApiResponse.fail(customException);
	}

	/// JSON 값 에러 처리
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ApiResponse<?> handleJsonException(HttpMessageNotReadableException exception) {

		/// 에러 이유 로그 찍기
		log.error(toLogMessage(exception.getMessage()));

		/// 기본 에러 코드로 응답 생성
		ErrorCode errorCode = CommonErrorCode.BAD_REQUEST_JSON;
		CustomException customException = new CustomException(errorCode);

		/// 응답
		return ApiResponse.fail(customException);
	}

	/// 타입 오류
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({UnexpectedTypeException.class, MethodArgumentTypeMismatchException.class})
	public ApiResponse<?> handleJUnexpectedTypeException(Exception exception) {

		/// 에러 이유 로그 찍기
		log.error(toLogMessage(exception.getMessage()));

		/// 기본 에러 코드로 응답 생성
		ErrorCode errorCode = CommonErrorCode.BAD_REQUEST_INVALID_INPUT;
		CustomException customException = new CustomException(errorCode);

		/// 응답
		return ApiResponse.fail(customException);
	}

	/// DB 문법 등 관련 문제 발생
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(InvalidDataAccessResourceUsageException.class)
	public ApiResponse<?> handleInvalidDataAccessResourceUsageException(
		InvalidDataAccessResourceUsageException exception) {

		/// 에러 이유 로그 찍기
		log.error(toLogMessage(exception.getMessage()));

		/// 기본 에러 코드로 응답 생성
		ErrorCode errorCode = CommonErrorCode.INTERNAL_DB_SERVER_ERROR;
		CustomException customException = new CustomException(errorCode);

		/// 응답
		return ApiResponse.fail(customException);
	}

	/// DB 스키마 관련 문제 발생
	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler({DataIntegrityViolationException.class, DuplicateKeyException.class})
	public ApiResponse<?> handleDataIntegrityViolationException(Exception exception) {

		/// 에러 이유 로그 찍기
		log.error(toLogMessage(exception.getMessage()));

		/// 기본 에러 코드로 응답 생성
		ErrorCode errorCode = CommonErrorCode.INTERNAL_DB_SCHEMA_SERVER_ERROR;
		CustomException customException = new CustomException(errorCode);

		/// 응답
		return ApiResponse.fail(customException);
	}

	/// DB 스키마 관련 문제 발생
	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(TransientDataAccessException.class)
	public ApiResponse<?> handleTransientDataAccessException(TransientDataAccessException exception) {

		/// 에러 이유 로그 찍기
		log.error(toLogMessage(exception.getMessage()));

		/// 기본 에러 코드로 응답 생성
		ErrorCode errorCode = CommonErrorCode.INTERNAL_DB_SERVER_ERROR;
		CustomException customException = new CustomException(errorCode);

		/// 응답
		return ApiResponse.fail(customException);
	}

	/// 최하위 에러 처리 (여기까지는 안오길 ...)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ApiResponse<?> handleException(Exception exception) {

		/// 에러 이유 로그 찍기
		log.error(toLogMessage(exception.getMessage()), exception);

		/// 기본 에러 코드로 응답 생성
		ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
		CustomException customException = new CustomException(errorCode);

		/// 응답
		return ApiResponse.fail(customException);
	}

	private String toLogMessage(String message) {
		return String.valueOf(sanitize(message));
	}

}
