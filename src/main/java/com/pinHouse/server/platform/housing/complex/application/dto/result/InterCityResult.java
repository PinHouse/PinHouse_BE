package com.pinHouse.server.platform.housing.complex.application.dto.result;
import lombok.Builder;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "[응답][경로] 시외 경로 탐색 결과 Response", description = "도시 간 다양한 교통수단의 경로 탐색 결과를 반환하는 DTO입니다.")
@Builder
public record InterCityResult(
        @Schema(description = "결과 구분(0-도시내, 1-도시간 직통, 2-도시간 환승)", example = "1")
        int searchType,

        @Schema(description = "탐색된 버스 경로 개수", example = "2")
        int busCount,

        @Schema(description = "탐색된 기차 경로 개수", example = "1")
        int trainCount,

        @Schema(description = "탐색된 항공 경로 개수", example = "0")
        int airCount,

        @Schema(description = "탐색된 혼합(환승) 경로 개수", example = "1")
        int mixedCount,

        @Schema(description = "거리", example = "1")
        double distance,

        @Schema(description = "도시 간 경로 리스트")
        List<RootResult> routes

) implements PathResult {}
