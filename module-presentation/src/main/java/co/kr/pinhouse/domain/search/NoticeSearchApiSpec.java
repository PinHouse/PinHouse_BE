package co.kr.pinhouse.domain.search;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.RequestParam;

import co.kr.pinhouse.common.auth.CurrentUserId;
import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.common.response.pageable.SliceRequest;
import co.kr.pinhouse.common.response.pageable.SliceResponse;
import co.kr.pinhouse.domain.search.application.dto.NoticeSearchFilterType;
import co.kr.pinhouse.domain.search.application.dto.response.NoticeSearchResultResponse;
import co.kr.pinhouse.domain.search.application.dto.NoticeSearchSortType;
import co.kr.pinhouse.domain.search.application.dto.response.PopularKeywordResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 공고 검색 API 명세
 */
@Tag(name = "공고 검색 API", description = "공고 제목 검색, 인기 검색어, 자동완성 기능")
public interface NoticeSearchApiSpec {

	/**
	 * 공고 검색 (무한 스크롤)
	 */
	@Operation(
		summary = "공고 제목 검색 API (무한 스크롤)",
		description = "MongoDB regex를 사용하여 공고 제목으로 검색합니다. 무한 스크롤 방식으로 결과를 반환합니다. 로그인한 사용자의 경우 좋아요 정보가 포함됩니다."
	)
	ApiResponse<SliceResponse<NoticeSearchResultResponse>> searchNotices(
		@Parameter(description = "검색 키워드", example = "행복주택", required = true)
		@RequestParam("q") String keyword,

		SliceRequest sliceRequest,

		@Parameter(description = "정렬 방식 (LATEST/최신공고순, END/마감임박순)", example = "LATEST")
		@RequestParam(required = false, defaultValue = "LATEST") NoticeSearchSortType sortType,

		@Parameter(description = "공고 상태 (ALL/전체, RECRUITING/모집중)", example = "ALL")
		@RequestParam(required = false, defaultValue = "ALL") NoticeSearchFilterType status,

		@CurrentUserId UUID userId
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

}
