package com.pinHouse.server.platform.housing.complex.application.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.pinHouse.server.platform.housing.complex.application.dto.result.RootResult;
import com.pinHouse.server.platform.housing.complex.application.dto.result.InterCityResult;
import com.pinHouse.server.platform.housing.complex.application.dto.result.TrainType;
import com.pinHouse.server.platform.housing.complex.application.dto.result.LineInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterCityResultParser {

    // =================
    //  퍼블릭 로직
    // =================

    /// 도시간 길찾기
    /// 도시간 길찾기
    public static InterCityResult parse(JsonNode root) {
        JsonNode result = root.path("result");

        int searchType = result.path("searchType").asInt(1);
        int busCount   = result.path("busCount").asInt(0);
        int trainCount = result.path("trainCount").asInt(0);
        int airCount   = result.path("airCount").asInt(0);
        int mixedCount = result.path("mixedCount").asInt(0);

        List<RootResult> routes = new ArrayList<>();

        double minDistance = Double.MAX_VALUE; // ✅ 대표 거리(최단 거리) 계산용

        JsonNode paths = result.path("path");
        if (paths.isArray()) {
            for (JsonNode path : paths) {
                JsonNode info = path.path("info");

                int totalTime = info.path("totalTime").asInt(0);

                // ✅ 도시간 응답은 totalPayment 사용
                int totalPayment = info.path("totalPayment")
                        .asInt(info.path("payment").asInt(0));

                // ✅ 이 경로의 총 이동 거리(없으면 trafficDistance로 fallback)
                double totalDistance = info.path("totalDistance")
                        .asDouble(info.path("trafficDistance").asDouble(0d));

                if (totalDistance > 0 && totalDistance < minDistance) {
                    minDistance = totalDistance;
                }

                List<RootResult.DistanceStep> steps = new ArrayList<>();
                JsonNode subPaths = path.path("subPath");
                if (subPaths.isArray()) {
                    for (JsonNode sub : subPaths) {
                        int trafficType = sub.path("trafficType").asInt();
                        RootResult.TransportType t = RootResult.TransportType.fromTrafficType(trafficType);

                        String lineInfo = null;
                        TrainType trainTypeEnum = null;

                        if (t == RootResult.TransportType.TRAIN) {
                            int trainTypeCode = sub.path("trainType").asInt(-1);
                            trainTypeEnum = TrainType.from(trainTypeCode);
                            lineInfo = trainTypeEnum.getLabel();
                        } else if (t == RootResult.TransportType.BUS) {
                            // 5: 고속, 6: 시외
                            lineInfo = (trafficType == 5) ? "고속버스" : "시외버스";
                        } else if (t == RootResult.TransportType.AIR) {
                            lineInfo = "항공";
                        }

                        // LineInfo 생성
                        LineInfo line = null;
                        if (trainTypeEnum != null) {
                            line = trainTypeEnum.toLineInfo();
                        }

                        steps.add(RootResult.DistanceStep.builder()
                                .type(t)
                                .time(sub.path("sectionTime").asInt(0))
                                .startName(sub.path("startName").asText(null))
                                .endName(sub.path("endName").asText(null))
                                .lineInfo(lineInfo)
                                .line(line)
                                .subwayLine(null)
                                .busRouteType(null)
                                .trainType(trainTypeEnum)
                                .expressBusType(null) // TODO: 필요 시 고속/시외버스 좌석 등급 파싱 추가
                                .build());
                    }
                }

                routes.add(RootResult.builder()
                        .totalTime(totalTime)
                        .totalPayment(totalPayment)
                        .totalDistance(totalDistance)
                        .steps(List.copyOf(steps))
                        .build());
            }
        }

        // path가 하나도 없으면 0으로 처리
        if (minDistance == Double.MAX_VALUE) {
            minDistance = 0d;
        }

        return InterCityResult.builder()
                .searchType(searchType)
                .busCount(busCount)
                .trainCount(trainCount)
                .airCount(airCount)
                .mixedCount(mixedCount)
                .distance(minDistance)
                .routes(List.copyOf(routes))
                .build();
    }



    // =================
    //  내부 로직
    // =================
    // 이전의 mapTransportType과 mapTrainType 메서드는 제거됨
    // TransportType.fromTrafficType()과 TrainType.from()을 직접 사용
}
