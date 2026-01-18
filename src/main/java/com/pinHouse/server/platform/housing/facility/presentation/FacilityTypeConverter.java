package com.pinHouse.server.platform.housing.facility.presentation;

import com.pinHouse.server.core.exception.code.FacilityErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

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
