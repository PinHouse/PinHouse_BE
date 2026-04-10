package co.kr.pinhouse.domain.search.domain.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;

import co.kr.pinhouse.common.exception.code.CommonErrorCode;
import co.kr.pinhouse.common.response.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/// 임대 유형
@RequiredArgsConstructor
@Getter
public enum SupplyType {

	HAPPY_HOUSING("행복주택", List.of("행복주택")),
	PUBLIC_RENTAL("공공임대", List.of("국민임대", "영구임대", "통합공공임대")),
	PRIVATE_RENTAL("민간임대", List.of("매입임대", "공공지원민간임대", "5년임대", "6년임대", "10년임대", "50년임대")),
	JEONSE_RENTAL("전세형 임대", List.of("장기전세", "전세임대"));

	private final String value;
	private final List<String> includedTypes;

	public static SupplyType fromValue(String value) {
		for (SupplyType type : values()) {
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
