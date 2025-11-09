package com.pinHouse.server.platform.search.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;

@Schema(name = "[응답][검색] 빠른 검색 응답", description = "빠른 검색 결과를 위한 응답 DTO입니다.")
@Builder
public record FastSearchResponse(
        long total,

        List<FastUnitTypeResponse> units
) {

        /// 정적 팩토리 메서드
        public static FastSearchResponse from(List<FastUnitTypeResponse> units) {
                return new FastSearchResponse(units.size(), units);
        }


}
