package co.kr.pinhouse.common.exception.code;

import org.springframework.http.HttpStatus;

import co.kr.pinhouse.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdErrorCode implements ErrorCode {

	NOT_FOUND_ADVERTISEMENT(404_400, HttpStatus.NOT_FOUND, "해당 광고를 찾을 수 없습니다."),
	BAD_REQUEST_AD_SCHEDULE(400_400, HttpStatus.BAD_REQUEST, "광고 노출 일정이 올바르지 않습니다.");

	private final Integer code;
	private final HttpStatus httpStatus;
	private final String message;
}
