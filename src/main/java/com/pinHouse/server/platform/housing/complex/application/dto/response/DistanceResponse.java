package com.pinHouse.server.platform.housing.complex.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pinHouse.server.platform.housing.complex.application.dto.result.RootResult;
import com.pinHouse.server.platform.housing.complex.application.dto.result.SubwayLineType;
import com.pinHouse.server.platform.housing.complex.application.dto.result.BusRouteType;
import com.pinHouse.server.platform.housing.complex.application.dto.result.TrainType;
import com.pinHouse.server.platform.housing.complex.application.dto.result.ExpressBusType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DistanceResponse(

        @Schema(description = "총 소요 시간(분)", example = "45")
        int totalTime,

        @Schema(description = "총 거리 (KM)", example = "17")
        double totalDistance,

        List<TransitResponse> routes,

        List<TransferPointResponse> stops
) {

    /// 정적 팩토리 메서드
    public static DistanceResponse from(RootResult rootResult, List<TransitResponse> routes) {
        return DistanceResponse.builder()
                .totalTime(rootResult.totalTime())
                .totalDistance(Math.round(rootResult.totalDistance() / 100.0) / 10.0)
                .routes(routes)
                .stops(null)
                .build();
    }

    /// 정적 팩토리 메서드
    public static DistanceResponse from(RootResult rootResult, List<TransitResponse> routes, List<TransferPointResponse> stops) {
        return DistanceResponse.builder()
                .totalTime(rootResult.totalTime())
                .totalDistance(Math.round(rootResult.totalDistance() / 100.0) / 10.0)
                .routes(routes)
                .stops(stops)
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

            @Schema(description = "지하철 노선 타입 (지하철인 경우)")
            SubwayLineType subwayLine,

            @Schema(description = "버스 노선 타입 (버스인 경우)")
            BusRouteType busRouteType,

            @Schema(description = "열차 타입 (열차인 경우)")
            TrainType trainType,

            @Schema(description = "고속/시외버스 좌석 등급 (고속/시외버스인 경우)")
            ExpressBusType expressBusType,

            @Schema(description = "세그먼트 배경 컬러(Hex 코드)", example = "#FF5722")
            String bgColorHex)
    { }

    @Builder
    public record TransferPointResponse(
            @Schema(description = "환승 역할 (START, TRANSFER, ARRIVAL)")
            TransferRole role,

            @Schema(description = "교통 타입 (WALK, BUS, SUBWAY, TRAIN, AIR)")
            ChipType type,

            @Schema(description = "정류장/역 이름")
            String stopName,

            @Schema(description = "노선 정보(버스번호/지하철 호선 등)")
            String lineText,

            @Schema(description = "지하철 노선 타입 (지하철인 경우)")
            SubwayLineType subwayLine,

            @Schema(description = "버스 노선 타입 (버스인 경우)")
            BusRouteType busRouteType,

            @Schema(description = "열차 타입 (열차인 경우)")
            TrainType trainType,

            @Schema(description = "고속/시외버스 좌석 등급 (고속/시외버스인 경우)")
            ExpressBusType expressBusType,

            @Schema(description = "배경 컬러(Hex 코드)")
            String bgColorHex
    ) {
        public enum TransferRole {
            START,     // 승차 지점
            TRANSFER,  // 환승 지점
            ARRIVAL    // 도착 지점
        }
    }
}
