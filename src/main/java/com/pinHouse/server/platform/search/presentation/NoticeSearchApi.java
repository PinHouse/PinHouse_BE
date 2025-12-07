package com.pinHouse.server.platform.search.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.core.response.response.pageable.SliceRequest;
import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchFilterType;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchResultResponse;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchSortType;
import com.pinHouse.server.platform.search.application.dto.PopularKeywordResponse;
import com.pinHouse.server.platform.search.application.usecase.NoticeSearchUseCase;
import com.pinHouse.server.platform.search.application.usecase.SearchKeywordUseCase;
import com.pinHouse.server.platform.search.presentation.swagger.NoticeSearchApiSpec;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 공고 검색 API
 */
@Slf4j
@RestController
@RequestMapping("/v1/search")
@RequiredArgsConstructor
public class NoticeSearchApi implements NoticeSearchApiSpec {

    private final NoticeSearchUseCase noticeSearchService;
    private final SearchKeywordUseCase searchKeywordService;

    /**
     * 공고 검색 (무한 스크롤)
     * GET /v1/search/notices?q=키워드&page=1&offSet=20&sortType=LATEST&status=ALL
     */
    @Override
    @GetMapping("/notices")
    public ApiResponse<SliceResponse<NoticeSearchResultResponse>> searchNotices(
            @RequestParam String q,
            SliceRequest sliceRequest,
            @RequestParam(required = false, defaultValue = "LATEST") NoticeSearchSortType sortType,
            @RequestParam(required = false, defaultValue = "ALL") NoticeSearchFilterType status,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        // 로그인하지 않은 경우 userId는 null
        UUID userId = (principalDetails != null) ? principalDetails.getId() : null;

        // 검색 실행
        SliceResponse<NoticeSearchResultResponse> response = noticeSearchService.searchNotices(
                q,
                sliceRequest.page(),
                sliceRequest.offSet(),
                sortType,
                status,
                userId
        );

        return ApiResponse.ok(response);
    }

    /**
     * 인기 검색어 조회
     * GET /v1/search/popular?limit=10
     */
    @Override
    @GetMapping("/popular")
    public ApiResponse<List<PopularKeywordResponse>> getPopularKeywords(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<PopularKeywordResponse> response = searchKeywordService.getPopularKeywords(limit);
        return ApiResponse.ok(response);
    }
}
