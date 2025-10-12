package com.pinHouse.server.platform.housing.notice.presentation.converter;

import com.pinHouse.server.platform.housing.notice.application.dto.SortType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DetailSortTypeConverter implements Converter<String, SortType.DetailSortType> {

    @Override
    public SortType.DetailSortType convert(String source) {
        return SortType.DetailSortType.from(source);
    }
}
