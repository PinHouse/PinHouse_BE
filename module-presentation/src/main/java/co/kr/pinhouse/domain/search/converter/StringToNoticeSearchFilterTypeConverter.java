package co.kr.pinhouse.domain.search.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import co.kr.pinhouse.domain.search.application.dto.NoticeSearchFilterType;

@Component
public class StringToNoticeSearchFilterTypeConverter implements Converter<String, NoticeSearchFilterType> {

	@Override
	public NoticeSearchFilterType convert(String source) {
		return NoticeSearchFilterType.from(source);
	}
}
