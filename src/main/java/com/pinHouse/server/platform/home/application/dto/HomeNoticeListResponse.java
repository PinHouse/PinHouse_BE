package com.pinHouse.server.platform.home.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(name = "[응답][홈] 홈 화면 공고 목록 조회 응답", description = "홈 화면에서 마감임박 공고 목록을 조회하기 위한 DTO입니다. SliceResponse와 유사한 구조에 region 필드를 추가했습니다.")
@Builder
public record HomeNoticeListResponse(

        @Schema(description = "공통 지역", example = "성남시")
        String region,

        @Schema(description = "공고 목록")
        List<HomeNoticeResponse> content,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext,

        @Schema(description = "전체 공고 개수", example = "100")
        long totalElements

) {
}
