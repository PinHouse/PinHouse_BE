package com.pinHouse.server.platform.search.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchResponse;
import com.pinHouse.server.platform.search.application.dto.PopularKeywordResponse;
import com.pinHouse.server.platform.search.application.dto.SearchSuggestionResponse;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 공고 검색 API 명세
 */
@Tag(name = "공고 검색 API", description = "공고 제목 검색, 인기 검색어, 자동완성 기능")
public interface NoticeSearchApiSpec {

    /**
     * 공고 검색
     */
    @Operation(
            summary = "공고 제목 검색 API",
            description = "MongoDB text search를 사용하여 공고 제목으로 검색합니다. 로그인한 사용자의 경우 좋아요 정보가 포함됩니다."
    )
    ApiResponse<NoticeSearchResponse> searchNotices(
            @Parameter(description = "검색 키워드", example = "행복주택", required = true)
            @RequestParam String q,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기 (최대 100)", example = "20")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "정렬 방식 (LATEST: 최신순, DEADLINE: 마감임박순)", example = "LATEST")
            @RequestParam(defaultValue = "LATEST") String sort,

            @Parameter(description = "필터 (OPEN: 모집중만, ALL: 전체)", example = "ALL")
            @RequestParam(defaultValue = "ALL") String filter,

            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    /**
     * 인기 검색어 조회
     */
    @Operation(
            summary = "인기 검색어 조회 API",
            description = "검색 횟수 기준 상위 N개의 인기 검색어를 조회합니다."
    )
    ApiResponse<List<PopularKeywordResponse>> getPopularKeywords(
            @Parameter(description = "조회할 검색어 개수", example = "10")
            @RequestParam(defaultValue = "10") int limit
    );

    /**
     * 검색어 자동완성
     */
    @Operation(
            summary = "검색어 자동완성 API",
            description = "입력한 접두어로 시작하는 인기 검색어를 제안합니다."
    )
    ApiResponse<SearchSuggestionResponse> getSuggestions(
            @Parameter(description = "검색어 접두어", example = "행복", required = true)
            @RequestParam String q,

            @Parameter(description = "제안할 검색어 개수", example = "5")
            @RequestParam(defaultValue = "5") int limit
    );
}
