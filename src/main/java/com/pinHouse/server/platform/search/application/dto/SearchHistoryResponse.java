package com.pinHouse.server.platform.search.application.dto;

import com.pinHouse.server.platform.search.domain.entity.SearchHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Optional;

@Builder
public record SearchHistoryResponse(

        @Schema(description = "존재 여부", example = "true")
        boolean existed,

        @Schema(description = "검색 ID", example = "1")
        String id

) {

    public static SearchHistoryResponse of(SearchHistory searchHistory) {

        /// 존재한다면
        return SearchHistoryResponse.builder()
                .existed(true)
                .id(searchHistory.getId())
                .build();
    }

    public static SearchHistoryResponse of() {

        /// 존재한다면
        return SearchHistoryResponse.builder()
                .existed(false)
                .id(null)
                .build();
    }

}
