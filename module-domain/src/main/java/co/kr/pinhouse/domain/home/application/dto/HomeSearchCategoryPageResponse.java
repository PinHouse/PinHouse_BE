package co.kr.pinhouse.domain.home.application.dto;

import java.util.List;

import co.kr.pinhouse.domain.search.application.dto.NoticeSearchResultResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "[응답][홈] 통합 검색 카테고리별 페이징 결과", description = "카테고리별 공고 검색 결과와 현재 페이지 정보를 반환합니다.")
public record HomeSearchCategoryPageResponse(

		@Schema(description = "검색 카테고리", example = "NOTICE")
		HomeSearchCategoryType category,

		@Schema(description = "현재 페이지 (1부터 시작)", example = "1")
		int page,

		@Schema(description = "공고 결과")
		List<NoticeSearchResultResponse> content,

		@Schema(description = "다음 페이지 존재 여부", example = "true")
		boolean hasNext
) {

	public static HomeSearchCategoryPageResponse notice(
			int page,
			List<NoticeSearchResultResponse> notices,
			boolean hasNext
	) {
		return HomeSearchCategoryPageResponse.builder()
				.category(HomeSearchCategoryType.NOTICE)
				.page(page)
				.content(notices)
				.hasNext(hasNext)
				.build();
	}

	public static HomeSearchCategoryPageResponse of(
			HomeSearchCategoryType category,
			int page,
			List<NoticeSearchResultResponse> content,
			boolean hasNext
	) {
		return HomeSearchCategoryPageResponse.builder()
				.category(category)
				.page(page)
				.content(content)
				.hasNext(hasNext)
				.build();
	}
}
