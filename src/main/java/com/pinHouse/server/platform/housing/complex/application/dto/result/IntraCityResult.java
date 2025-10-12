package com.pinHouse.server.platform.housing.complex.application.dto.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record IntraCityResult(

        @Schema(description = "결과 구분(0-도시내, 1-도시간 직통, 2-도시간 환승)", example = "0")
        int searchType,

        @Schema(description = "버스 결과 개수", example = "8")
        int busCount,

        @Schema(description = "지하철 결과 개수", example = "10")
        int subwayCount,

        @Schema(description = "버스+지하철 결과 개수", example = "5")
        int subwayBusCount,

        @Schema(description = "출발-도착 직선거리(m)", example = "1245.6")
        double pointDistance,

        @Schema(description = "도시내 경로 리스트")
        List<RootResult> routes

) implements PathResult {

}
