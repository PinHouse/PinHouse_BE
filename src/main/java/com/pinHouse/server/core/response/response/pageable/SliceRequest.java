package com.pinHouse.server.core.response.response.pageable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "[요청][페이징] 무한 스크롤 페이징", description = "무한 스크롤 방식의 페이징 처리를 위한 요청 DTO")
public record SliceRequest(

        @Schema(description = "페이지 번호 (0부터 시작)", example = "0", defaultValue = "1")
        int page,

        @Schema(description = "한 페이지에 조회할 데이터 개수", example = "10", defaultValue = "10")
        int offSet) {
}
