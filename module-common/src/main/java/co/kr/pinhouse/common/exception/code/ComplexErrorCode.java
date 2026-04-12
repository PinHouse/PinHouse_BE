package co.kr.pinhouse.common.exception.code;

import org.springframework.http.HttpStatus;

import co.kr.pinhouse.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ComplexErrorCode implements ErrorCode {

	// ========================
	// 400 Bad Request
	// ========================
	BAD_REQUEST_DISTANCE(400_000, HttpStatus.BAD_REQUEST, "입력 좌표에서 인코딩 문제가 생겼습니다"),
	BAD_REQUEST_DEPOSIT(400_001, HttpStatus.BAD_REQUEST, "입력 유형의 공급 정보가 없습니다."),

	// ========================
	// 401 Unauthorized
	// ========================

	// ========================
	// 403 Forbidden
	// ========================

	// ========================
	// 404 Not Found
	// ========================
	NOT_FOUND_COMPLEX(404_001, HttpStatus.NOT_FOUND, "해당 임대주택을 찾을 수 없습니다"),
	NOT_FOUND_UNITTYPE(404_002, HttpStatus.NOT_FOUND, "해당 방을 찾을 수 없습니다"),
	NOT_FOUND_TRANSIT_ROUTE(404_003, HttpStatus.NOT_FOUND, "해당 위치 간 대중교통 경로를 찾을 수 없습니다"),

	// ========================
	// 500 Server Error
	// ========================
	ODSAY_SERVER_ERROR(500_001, HttpStatus.INTERNAL_SERVER_ERROR, "ODsay API 호출 실패"),
	ODSAY_PARSING_ERROR(500_002, HttpStatus.INTERNAL_SERVER_ERROR, "ODsay API 응답 처리 실패"),
	ODSAY_ERROR_RESPONSE(500_003, HttpStatus.INTERNAL_SERVER_ERROR, "ODsay API 에러 응답 수신"),
	ODSAY_INVALID_API_KEY(500_004, HttpStatus.INTERNAL_SERVER_ERROR, "ODsay API Key가 올바르지 않습니다");

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
