package com.pinHouse.server.platform.housing.notice.presentation.converter;

import com.pinHouse.server.platform.housing.notice.application.dto.SortType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ListSortTypeConverter implements Converter<String, SortType.ListSortType> {

    @Override
    public SortType.ListSortType convert(String source) {
        return SortType.ListSortType.from(source);
    }
}
