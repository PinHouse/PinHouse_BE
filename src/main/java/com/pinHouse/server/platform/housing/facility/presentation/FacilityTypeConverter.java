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
        String s = source.trim();

        /// 한글 라벨 매칭
        for (FacilityType t : FacilityType.values()) {
            /// getValue()는 "공원", "도서관" 등
            if (t.getValue().equals(s)) return t;
        }
        /// 영문 enum 이름 매칭도 허용하고 싶으면:
        try {
            return FacilityType.valueOf(s.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignore) {}

        throw new CustomException(FacilityErrorCode.BAD_REQUEST_INPUT_FACILITY);
    }
}
