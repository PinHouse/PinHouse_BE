package com.pinHouse.common.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.pinHouse.domain.search.application.dto.NoticeSearchFilterType;

/**
 * String을 NoticeSearchFilterType으로 변환하는 커스텀 컨버터
 * Enum 이름 또는 한글 라벨 모두 지원
 */
@Component
public class StringToNoticeSearchFilterTypeConverter implements Converter<String, NoticeSearchFilterType> {

	@Override
	public NoticeSearchFilterType convert(String source) {
		return NoticeSearchFilterType.from(source);
	}
}
