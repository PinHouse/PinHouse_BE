package com.pinHouse.server.core.config;

import com.pinHouse.server.platform.housing.notice.presentation.converter.DetailSortTypeConverter;
import com.pinHouse.server.platform.housing.notice.presentation.converter.ListSortTypeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ListSortTypeConverter listSortTypeConverter;
    private final DetailSortTypeConverter detailSortTypeConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {

        /// 컨버터 추가하기
        registry.addConverter(listSortTypeConverter);
        registry.addConverter(detailSortTypeConverter);
    }
}
