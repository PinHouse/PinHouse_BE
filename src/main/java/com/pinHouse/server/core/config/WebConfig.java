package com.pinHouse.server.core.config;

import com.pinHouse.server.platform.housing.notice.presentation.converter.SortTypeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final SortTypeConverter sortTypeConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(sortTypeConverter);
    }
}
