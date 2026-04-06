package co.kr.pinhouse.domain.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import co.kr.pinhouse.common.exception.code.SecurityErrorCode;
import co.kr.pinhouse.common.response.CustomException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Provider {
	KAKAO("카카오"),
	NAVER("네이버");

	private final String label;

	@JsonCreator
	public static Provider fromLabel(String label) {
		for (Provider provider : Provider.values()) {
			if (provider.getLabel().equals(label)) {
				return provider;
			}
		}
		throw new CustomException(SecurityErrorCode.BAD_REQUEST_OAUTH2);
	}

	@JsonValue
	public String getLabel() {
		return label;
	}

}
