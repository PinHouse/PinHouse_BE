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

                /// 없다면 응답
                if (units == null || units.isEmpty()) {
                        return new FastSearchResponse(0, List.of());
                }

                return new FastSearchResponse(units.size(), units);
        }


}
