package com.pinHouse.server.core.response.response.pageable;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "무한스크롤 요청 DTO")
public record SliceRequest(

        @Schema(description = "페이지 시작", example = "1")
        int page,

        @Schema(description = "조회할 개수", example = "10")
        int offSet) {
}
