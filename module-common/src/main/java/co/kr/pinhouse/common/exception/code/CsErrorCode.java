package co.kr.pinhouse.common.exception.code;

import org.springframework.http.HttpStatus;

import co.kr.pinhouse.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CsErrorCode implements ErrorCode {

	NOT_FOUND_INQUIRY(404_300, HttpStatus.NOT_FOUND, "해당 문의를 찾을 수 없습니다."),
	FORBIDDEN_INQUIRY_ACCESS(403_300, HttpStatus.FORBIDDEN, "해당 문의에 접근할 권한이 없습니다.");

	private final Integer code;
	private final HttpStatus httpStatus;
	private final String message;
}
