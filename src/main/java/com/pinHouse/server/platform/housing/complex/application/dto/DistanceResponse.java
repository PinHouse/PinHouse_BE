package com.pinHouse.server.platform.housing.complex.application.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
@Schema(name = "[응답][거리] 거리 및 이동 정보 응답", description = "총 소요 시간, 총 요금, 환승 횟수 등을 포함한 거리 응답 DTO입니다.")
public record DistanceResponse(
        
        @Schema(description = "총 소요 시간(분)", example = "45")
        int totalTime,

        @Schema(description = "총 요금(원)", example = "1350")
        int totalPayment,

        @Schema(description = "총 도보 거리(미터)", example = "800")
        int totalWalk,

        @Schema(description = "버스 환승 횟수", example = "1")
        int busCount,

        @Schema(description = "지하철 환승 횟수", example = "2")
        int subwayCount,

        @Schema(description = "구간별 이동 단계 리스트")
        List<DistanceStep> steps
) {

    /// 정적 팩토리 메서드
    public static List<DistanceResponse> of(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            List<DistanceResponse> responses = new ArrayList<>();
            for (JsonNode pathNode : root.path("result").path("path")) {
                JsonNode info = pathNode.path("info");

                // steps 변환
                List<DistanceStep> steps = new ArrayList<>();
                for (JsonNode sub : pathNode.path("subPath")) {
                    int trafficType = sub.path("trafficType").asInt();
                    TransportType type = switch (trafficType) {
                        case 1 -> TransportType.SUBWAY;
                        case 2 -> TransportType.BUS;
                        default -> TransportType.WALK;
                    };

                    String lineInfo = null;
                    if (type == TransportType.BUS && sub.has("lane")) {
                        lineInfo = sub.path("lane").get(0).path("busNo").asText();
                    } else if (type == TransportType.SUBWAY && sub.has("lane")) {
                        lineInfo = sub.path("lane").get(0).path("name").asText();
                    }

                    List<String> stations = new ArrayList<>();
                    if (sub.has("passStopList")) {
                        for (JsonNode st : sub.path("passStopList").path("stations")) {
                            stations.add(st.path("stationName").asText());
                        }
                    }

                    steps.add(DistanceStep.builder()
                            .type(type)
                            .time(sub.path("sectionTime").asInt())
                            .distance(sub.path("distance").asInt())
                            .startName(sub.path("startName").asText(null))
                            .endName(sub.path("endName").asText(null))
                            .lineInfo(lineInfo)
                            .stations(stations)
                            .build());
                }

                DistanceResponse response = DistanceResponse.builder()
                        .totalTime(info.path("totalTime").asInt())
                        .totalPayment(info.path("payment").asInt())
                        .totalWalk(info.path("totalWalk").asInt())
                        .busCount(info.path("busTransitCount").asInt())
                        .subwayCount(info.path("subwayTransitCount").asInt())
                        .steps(steps)
                        .build();

                responses.add(response);
            }
            return responses;
        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 실패", e);
        }
    }

    @Builder
    @Schema(name = "[응답][거리 단계] 거리 단계 정보 응답", description = "거리 단계 정보를 나타내는 DTO입니다.")
    protected record DistanceStep(

            @Schema(description = "타입", example = "15")
            TransportType type,

            @Schema(description = "소요 시간(분)", example = "15")
            int time,

            @Schema(description = "이동 거리(m)", example = "1200")
            int distance,

            @Schema(description = "출발 지점명", example = "서울역")
            String startName,

            @Schema(description = "도착 지점명", example = "강남역")
            String endName,

            @Schema(description = "경유 정류장/역", example = "[\"을지로입구\", \"시청\"]", required = false)
            List<String> stations,

            @Schema(description = "버스 번호, 지하철 노선명 등", example = "100번, 2호선")
            String lineInfo

    ) {
    }


    protected enum TransportType {
        WALK, BUS, SUBWAY
    }
}


