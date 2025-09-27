package com.pinHouse.server.platform.housing.distance.application.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Builder
public record DistanceResponse(
        int totalTime,
        int totalPayment,       // 총 요금
        int totalWalk,        // 총 도보 거리 (m)
        int busCount,          // 버스 환승 횟수
        int subwayCount,        // 지하철 환승 횟수
        List<StepDto> steps    // 구간별 이동 단계
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
                List<StepDto> steps = new ArrayList<>();
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

                    steps.add(StepDto.builder()
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
}
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    class StepDto {
        private TransportType type;   // 이동 수단 타입 (도보/버스/지하철)
        private int time;             // 소요 시간(분)
        private int distance;         // 이동 거리 (m)
        private String startName;     // 출발 지점명
        private String endName;       // 도착 지점명
        private List<String> stations;// 경유 정류장/역 (optional)
        private String lineInfo;      // 버스 번호, 지하철 노선명 등
    }


    enum TransportType {
        WALK, BUS, SUBWAY
    }

