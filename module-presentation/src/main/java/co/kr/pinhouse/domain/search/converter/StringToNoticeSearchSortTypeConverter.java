package co.kr.pinhouse.domain.search.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import co.kr.pinhouse.domain.search.application.dto.NoticeSearchSortType;

@Component
public class StringToNoticeSearchSortTypeConverter implements Converter<String, NoticeSearchSortType> {

	@Override
	public NoticeSearchSortType convert(String source) {
		return NoticeSearchSortType.from(source);
	}
}
