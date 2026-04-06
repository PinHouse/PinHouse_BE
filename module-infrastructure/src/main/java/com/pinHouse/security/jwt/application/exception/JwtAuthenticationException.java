package com.pinHouse.security.jwt.application.exception;

import org.springframework.security.core.AuthenticationException;

import com.pinHouse.common.response.ErrorCode;

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
