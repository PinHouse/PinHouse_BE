package co.kr.pinhouse.domain.search.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import co.kr.pinhouse.common.exception.code.CommonErrorCode;
import co.kr.pinhouse.common.response.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum HouseType {

	APARTMENT("아파트"),
	OFFICE("오피스텔"),
	DORMITORY("기숙사"),
	MULTI_FAMILY("다세대주택"),
	ROW_HOUSE("연립주택"),
	DETACHED_HOUSE("단독주택");

	private final String value;

	@JsonCreator
	public static HouseType fromValue(String value) {
		for (HouseType type : values()) {
			if (type.value.equals(value)) {
				return type;
			}
		}
		throw new CustomException(CommonErrorCode.BAD_PARAMETER);
	}

	@JsonValue
	public String getValue() {
		return value;
	}
}
