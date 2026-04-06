package com.pinHouse.common.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.pinHouse.common.exception.code.FacilityErrorCode;
import com.pinHouse.common.response.CustomException;
import com.pinHouse.domain.housing.facility.domain.entity.FacilityType;

@Component
public class FacilityTypeConverter implements Converter<String, FacilityType> {
	@Override
	public FacilityType convert(String source) {
		// null/trim 처리
		if (source == null) {
			throw new CustomException(FacilityErrorCode.BAD_REQUEST_INPUT_FACILITY);
		}
		return FacilityType.fromValue(source.trim());
	}
}
