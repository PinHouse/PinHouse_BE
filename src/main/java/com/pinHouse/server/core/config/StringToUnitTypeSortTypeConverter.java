package com.pinHouse.server.core.config;

import com.pinHouse.server.platform.housing.notice.application.dto.UnitTypeSortType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

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
