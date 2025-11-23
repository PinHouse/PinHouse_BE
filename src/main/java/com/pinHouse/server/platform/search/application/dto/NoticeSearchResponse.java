package com.pinHouse.server.platform.search.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 공고 검색 응답 DTO (페이징 포함)
 */
@Builder
@Schema(name = "[응답][검색] 공고 검색 결과 목록", description = "공고 검색 결과 목록 (페이징 포함)")
public record NoticeSearchResponse(
        @Schema(description = "검색 결과 목록")
        List<NoticeSearchResultResponse> notices,

        @Schema(description = "전체 검색 결과 수", example = "150")
        long totalElements,

        @Schema(description = "전체 페이지 수", example = "8")
        int totalPages,

        @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
        int currentPage,

        @Schema(description = "페이지 크기", example = "20")
        int pageSize,

        @Schema(description = "검색 키워드", example = "행복주택")
        String keyword
) {

    /**
     * Page 객체에서 NoticeSearchResponse 생성
     */
    public static NoticeSearchResponse from(Page<NoticeSearchResultResponse> page, String keyword) {
        return NoticeSearchResponse.builder()
                .notices(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .keyword(keyword)
                .build();
    }
}
