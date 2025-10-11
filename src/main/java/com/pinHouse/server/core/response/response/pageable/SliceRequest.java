package com.pinHouse.server.core.response.response.pageable;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "무한스크롤 요청 DTO")
public record SliceRequest(

        @Schema(description = "마지막 ID", example = "null")
        Long lastId,

        @Schema(description = "조회할 개수", example = "10")
        int offSet) {
}
