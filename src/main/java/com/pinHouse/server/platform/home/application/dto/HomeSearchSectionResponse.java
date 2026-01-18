package com.pinHouse.server.platform.home.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(name = "[응답][홈] 통합 검색 섹션", description = "홈 통합 검색 섹션별 결과입니다.")
public record HomeSearchSectionResponse<T>(

        @Schema(description = "결과 목록")
        List<T> content,

        @Schema(description = "다음 페이지 존재 여부", example = "false")
        boolean hasNext
) {

    public static <T> HomeSearchSectionResponse<T> of(List<T> content, boolean hasNext) {
        return HomeSearchSectionResponse.<T>builder()
                .content(content)
                .hasNext(hasNext)
                .build();
    }
}
