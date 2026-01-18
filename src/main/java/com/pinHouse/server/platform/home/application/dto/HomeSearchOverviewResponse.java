package com.pinHouse.server.platform.home.application.dto;

import com.pinHouse.server.platform.search.application.dto.NoticeSearchResultResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "[응답][홈] 통합 검색 미리보기", description = "홈 통합 검색 섹션별 상위 결과(공고)를 반환합니다.")
public record HomeSearchOverviewResponse(

        @Schema(description = "공고명 검색 결과 (5개 미리보기)")
        HomeSearchSectionResponse<NoticeSearchResultResponse> notices,

        @Schema(description = "단지명 검색 결과 (5개 미리보기, 공고 기준)")
        HomeSearchSectionResponse<NoticeSearchResultResponse> complexes,

        @Schema(description = "모집대상 검색 결과 (5개 미리보기, 공고 기준)")
        HomeSearchSectionResponse<NoticeSearchResultResponse> targetGroups,

        @Schema(description = "지역 검색 결과 (5개 미리보기, 공고 기준)")
        HomeSearchSectionResponse<NoticeSearchResultResponse> regions,

        @Schema(description = "주택유형 검색 결과 (5개 미리보기, 공고 기준)")
        HomeSearchSectionResponse<NoticeSearchResultResponse> houseTypes
) {
}
