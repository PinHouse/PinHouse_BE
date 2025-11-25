package com.pinHouse.server.platform.housing.complex.application.dto.result;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinHouse.server.core.exception.code.ComplexErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Builder
@Schema(name = "[응답][거리] 거리 및 이동 정보 응답", description = "총 소요 시간, 총 요금, 환승 횟수 등을 포함한 거리 응답 DTO입니다.")
public record RootResult(
        
        @Schema(description = "총 소요 시간(분)", example = "45")
        int totalTime,

        @Schema(description = "총 요금(원)", example = "1350")
        int totalPayment,

        @Schema(description = "총 이동 거리(m)", example = "28598")
        double totalDistance,

        @Schema(description = "구간별 이동 단계 리스트")
        List<DistanceStep> steps
) {

    /// 정적 팩토리 메서드
    public static List<RootResult> of(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            List<RootResult> responses = new ArrayList<>();
            for (JsonNode pathNode : root.path("result").path("path")) {
                JsonNode info = pathNode.path("info");

                // steps 변환
                List<DistanceStep> steps = new ArrayList<>();
                for (JsonNode sub : pathNode.path("subPath")) {
                    int trafficType = sub.path("trafficType").asInt();
                    TransportType type = TransportType.fromTrafficType(trafficType);

                    String lineInfo = null;
                    SubwayLineType subwayLine = null;
                    BusRouteType busRouteType = null;

                    if (type == TransportType.BUS && sub.has("lane")) {
                        lineInfo = sub.path("lane").get(0).path("busNo").asText();
                        String busTypeStr = sub.path("lane").get(0).path("type").asText(null);
                        busRouteType = BusRouteType.from(busTypeStr);
                    } else if (type == TransportType.SUBWAY && sub.has("lane")) {
                        lineInfo = sub.path("lane").get(0).path("name").asText();
                        String subwayCodeStr = sub.path("lane").get(0).path("subwayCode").asText(null);
                        subwayLine = SubwayLineType.from(subwayCodeStr);
                    }

                    steps.add(DistanceStep.builder()
                            .type(type)
                            .time(sub.path("sectionTime").asInt())
                            .distance(sub.path("distance").asInt())
                            .startName(sub.path("startName").asText(null))
                            .endName(sub.path("endName").asText(null))
                            .lineInfo(lineInfo)
                            .subwayLine(subwayLine)
                            .busRouteType(busRouteType)
                            .trainType(null)
                            .expressBusType(null)
                            .build());
                }

                /// ODsay info.totalDistance 사용
                int totalDistance = info.path("totalDistance").asInt(0);


                RootResult response = RootResult.builder()
                        .totalTime(info.path("totalTime").asInt())
                        .totalPayment(info.path("payment").asInt())
                        .totalDistance(totalDistance)
                        .steps(steps)
                        .build();

                responses.add(response);
            }
            return responses;
        } catch (Exception e) {
            throw new CustomException(ComplexErrorCode.ODSAY_PARSING_ERROR);
        }

    }

    @Builder
    @Schema(name = "[응답][거리 단계] 거리 단계 정보 응답", description = "거리 단계 정보를 나타내는 DTO입니다.")
    public record DistanceStep(

            @Schema(description = "타입", example = "SUBWAY")
            TransportType type,

            @Schema(description = "소요 시간(분)", example = "15")
            int time,

            @Schema(description = "이동 거리(m)", example = "1200")
            int distance,

            @Schema(description = "출발 지점명", example = "서울역")
            String startName,

            @Schema(description = "도착 지점명", example = "강남역")
            String endName,

            @Schema(description = "버스 번호, 지하철 노선명 등", example = "100번, 2호선")
            String lineInfo,

            @Schema(description = "지하철 노선 타입 (지하철인 경우)")
            SubwayLineType subwayLine,

            @Schema(description = "버스 노선 타입 (버스인 경우)")
            BusRouteType busRouteType,

            @Schema(description = "열차 타입 (열차인 경우)")
            TrainType trainType,

            @Schema(description = "고속/시외버스 좌석 등급 (고속/시외버스인 경우)")
            ExpressBusType expressBusType

    ) {
    }


    public enum TransportType {
        WALK, BUS, SUBWAY, TRAIN, AIR, UNKNOWN;

        /**
         * trafficType 코드로부터 TransportType을 조회
         * @param trafficType 교통 수단 코드 (1:지하철, 2:버스, 3:도보, 4:열차, 5:고속버스, 6:시외버스, 7:항공)
         * @return 매칭되는 TransportType
         */
        public static TransportType fromTrafficType(int trafficType) {
            return switch (trafficType) {
                case 1 -> SUBWAY;
                case 2 -> BUS;
                case 3 -> WALK;
                case 4 -> TRAIN;
                case 5, 6 -> BUS; // 고속버스, 시외버스 -> BUS로 통합
                case 7 -> AIR;
                default -> UNKNOWN;
            };
        }
    }
}


