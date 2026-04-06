package co.kr.pinhouse.security.jwt.application.exception;

import org.springframework.security.core.AuthenticationException;

import co.kr.pinhouse.common.response.ErrorCode;
import lombok.Getter;

@Getter
public class JwtAuthenticationException extends AuthenticationException {

	private final ErrorCode errorCode;

	/// 생성자
	public JwtAuthenticationException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
