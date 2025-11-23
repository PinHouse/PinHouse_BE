package com.pinHouse.server.platform.search.application.dto;

import com.pinHouse.server.platform.search.domain.entity.SearchKeyword;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

/**
 * 인기 검색어 응답 DTO
 */
@Builder
@Schema(name = "[응답][검색] 인기 검색어", description = "인기 검색어 정보")
public record PopularKeywordResponse(
        @Schema(description = "검색어", example = "행복주택")
        String keyword,

        @Schema(description = "검색 횟수", example = "1542")
        Long count,

        @Schema(description = "마지막 검색 시각", example = "2024-01-15T10:30:00Z")
        Instant lastSearchedAt
) {

    /**
     * SearchKeyword에서 PopularKeywordResponse 생성
     */
    public static PopularKeywordResponse from(SearchKeyword searchKeyword) {
        return PopularKeywordResponse.builder()
                .keyword(searchKeyword.getKeyword())
                .count(searchKeyword.getCount())
                .lastSearchedAt(searchKeyword.getLastSearchedAt())
                .build();
    }

    /**
     * SearchKeyword 리스트에서 PopularKeywordResponse 리스트 생성
     */
    public static List<PopularKeywordResponse> from(List<SearchKeyword> searchKeywords) {
        return searchKeywords.stream()
                .map(PopularKeywordResponse::from)
                .toList();
    }
}
