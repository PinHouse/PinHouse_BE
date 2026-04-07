package co.kr.pinhouse.domain.search;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.kr.pinhouse.common.auth.CurrentUserId;
import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.common.response.pageable.SliceRequest;
import co.kr.pinhouse.common.response.pageable.SliceResponse;
import co.kr.pinhouse.domain.search.application.dto.NoticeSearchFilterType;
import co.kr.pinhouse.domain.search.application.dto.response.NoticeSearchResultResponse;
import co.kr.pinhouse.domain.search.application.dto.NoticeSearchSortType;
import co.kr.pinhouse.domain.search.application.dto.response.PopularKeywordResponse;
import co.kr.pinhouse.domain.search.application.usecase.NoticeSearchUseCase;
import co.kr.pinhouse.domain.search.application.usecase.SearchKeywordUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
		@RequestParam("q") String keyword,
		SliceRequest sliceRequest,
		@RequestParam(required = false, defaultValue = "LATEST") NoticeSearchSortType sortType,
		@RequestParam(required = false, defaultValue = "ALL") NoticeSearchFilterType status,
		@CurrentUserId UUID userId
	) {
		// 로그인하지 않은 경우 userId는 null

		// 검색 실행
		SliceResponse<NoticeSearchResultResponse> response = noticeSearchService.searchNotices(
			keyword,
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
