package co.kr.pinhouse.common.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import co.kr.pinhouse.domain.housing.notice.application.dto.UnitTypeSortType;

/**
 * String을 UnitTypeSortType으로 변환하는 커스텀 컨버터
 * Enum 이름 또는 한글 라벨 모두 지원
 */
@Component
public class StringToUnitTypeSortTypeConverter implements Converter<String, UnitTypeSortType> {

	@Override
	public UnitTypeSortType convert(String source) {
		return UnitTypeSortType.from(source);
	}
}
