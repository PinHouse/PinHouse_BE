package com.pinHouse.server.platform.housing.complex.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

/**
 * 대중교통 경로 전체 정보 (총 시간/거리 + 구간별 정보)
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "[응답][대중교통] 경로 전체 정보", description = "대중교통 경로의 총 시간/거리와 구간별 세부 정보")
public record TransitInfoResponse(

        @Schema(description = "총 소요 시간 (텍스트)", example = "약 1시간 23분")
        String totalTime,

        @Schema(description = "총 소요 시간 (분)", example = "83")
        Integer totalTimeMinutes,

        @Schema(description = "총 거리 (km)", example = "39.6")
        Double totalDistance,

        @Schema(description = "구간별 세부 정보")
        List<TransitRoutesResponse.SegmentResponse> segments
) {
}
