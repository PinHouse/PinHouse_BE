package co.kr.pinhouse.presentation.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import co.kr.pinhouse.common.exception.code.FacilityErrorCode;
import co.kr.pinhouse.common.response.CustomException;
import co.kr.pinhouse.domain.housing.facility.domain.entity.FacilityType;

@Component
public class FacilityTypeConverter implements Converter<String, FacilityType> {
	@Override
	public FacilityType convert(String source) {
		if (source == null) {
			throw new CustomException(FacilityErrorCode.BAD_REQUEST_INPUT_FACILITY);
		}
		return FacilityType.fromValue(source.trim());
	}
}
