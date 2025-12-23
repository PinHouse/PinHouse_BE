package com.pinHouse.server.platform.housing.complex.application.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pinHouse.server.core.util.TimeFormatter;
import com.pinHouse.server.platform.housing.complex.application.dto.result.RootResult;
import com.pinHouse.server.platform.housing.complex.application.dto.result.SubwayLineType;
import com.pinHouse.server.platform.housing.complex.application.dto.result.BusRouteType;
import com.pinHouse.server.platform.housing.complex.application.dto.result.TrainType;
import com.pinHouse.server.platform.housing.complex.application.dto.result.ExpressBusType;
import com.pinHouse.server.platform.housing.complex.application.dto.result.LineInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DistanceResponse(

        @Schema(description = "총 소요 시간 (포맷팅)", example = "1시간 30분")
        String totalTime,

        @Schema(description = "총 소요 시간(분) - 내부 필터링용", example = "45", hidden = true)
        int totalTimeMinutes,

        @Schema(description = "총 거리 (KM)", example = "17")
        double totalDistance,

        @Schema(description = "교통 구간 정보 목록")
        List<TransitResponse> routes
) {

    /// 정적 팩토리 메서드
    public static DistanceResponse from(RootResult rootResult, List<TransitResponse> routes) {
        int minutes = rootResult.totalTime();
        return DistanceResponse.builder()
                .totalTime(TimeFormatter.formatTimeOrNull(minutes))
                .totalTimeMinutes(minutes)
                .totalDistance(Math.round(rootResult.totalDistance() / 100.0) / 10.0)
                .routes(routes)
                .build();
    }





    @Schema(name = "[응답][교통] 세그먼트 구간 정보 Response", description = "제일 빠른 교통수단의 소요시간, 노선, 배경색 정보를 포함한 응답 DTO입니다.")
    @Builder
    public record TransitResponse(

            @Schema(description = "교통 타입 (WALK, BUS, SUBWAY, TRAIN, AIR)", example = "BUS")
            ChipType type,

            @Schema(description = "구간 소요 시간 텍스트", example = "12분")
            String minutesText,

            @Schema(description = "노선 정보(버스번호/지하철 호선 등), 없는 경우 null", example = "9401, G8110")
            String lineText,

            @Schema(description = "통합 노선 정보 (코드, 이름, 색상)")
            @JsonIgnore
            LineInfo line,

            @Schema(hidden = true)
            @com.fasterxml.jackson.annotation.JsonIgnore
            SubwayLineType subwayLine,

            @Schema(hidden = true)
            @com.fasterxml.jackson.annotation.JsonIgnore
            BusRouteType busRouteType,

            @Schema(hidden = true)
            @com.fasterxml.jackson.annotation.JsonIgnore
            TrainType trainType,

            @Schema(hidden = true)
            @com.fasterxml.jackson.annotation.JsonIgnore
            ExpressBusType expressBusType,

            @Schema(description = "세그먼트 배경 컬러(Hex 코드)", example = "#FF5722")
            @JsonIgnore
            String bgColorHex)
    { }

}
