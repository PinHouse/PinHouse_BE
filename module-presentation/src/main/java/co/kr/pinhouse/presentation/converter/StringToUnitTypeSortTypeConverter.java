package co.kr.pinhouse.presentation.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import co.kr.pinhouse.domain.housing.notice.application.dto.UnitTypeSortType;

@Component
public class StringToUnitTypeSortTypeConverter implements Converter<String, UnitTypeSortType> {

	@Override
	public UnitTypeSortType convert(String source) {
		return UnitTypeSortType.from(source);
	}
}
