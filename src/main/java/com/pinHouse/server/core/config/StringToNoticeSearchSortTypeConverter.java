package com.pinHouse.server.core.config;

import com.pinHouse.server.platform.search.application.dto.NoticeSearchSortType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * String을 NoticeSearchSortType으로 변환하는 커스텀 컨버터
 * Enum 이름 또는 한글 라벨 모두 지원
 */
@Component
public class StringToNoticeSearchSortTypeConverter implements Converter<String, NoticeSearchSortType> {

    @Override
    public NoticeSearchSortType convert(String source) {
        return NoticeSearchSortType.from(source);
    }
}
