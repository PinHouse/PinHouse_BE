package com.pinHouse.server.platform.search.application.dto;

import com.pinHouse.server.platform.search.domain.entity.SearchKeyword;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

/**
 * 검색어 자동완성 응답 DTO
 */
@Builder
@Schema(name = "[응답][검색] 검색어 자동완성", description = "검색어 자동완성 제안 목록")
public record SearchSuggestionResponse(
        @Schema(description = "제안 검색어 목록")
        List<String> suggestions
) {

    /**
     * SearchKeyword 리스트에서 SearchSuggestionResponse 생성
     */
    public static SearchSuggestionResponse from(List<SearchKeyword> searchKeywords) {
        List<String> keywords = searchKeywords.stream()
                .map(SearchKeyword::getKeyword)
                .toList();

        return SearchSuggestionResponse.builder()
                .suggestions(keywords)
                .build();
    }
}
