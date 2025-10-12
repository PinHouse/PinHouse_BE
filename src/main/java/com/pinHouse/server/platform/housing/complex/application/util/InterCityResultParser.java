package com.pinHouse.server.platform.housing.complex.application.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.pinHouse.server.platform.housing.complex.application.dto.result.RootResult;
import com.pinHouse.server.platform.housing.complex.application.dto.result.InterCityResult;
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
    public static InterCityResult parse(JsonNode root) {
        JsonNode result = root.path("result");

        /// 값 추출
        int searchType = result.path("searchType").asInt(1); // 1-도시간
        int busCount   = result.path("busCount").asInt(0);
        int trainCount = result.path("trainCount").asInt(0);
        int airCount   = result.path("airCount").asInt(0);
        int mixedCount = result.path("mixedCount").asInt(0);

        List<RootResult> routes = new ArrayList<>();
        JsonNode paths = result.path("path");
        if (paths.isArray()) {
            for (JsonNode path : paths) {
                JsonNode info = path.path("info");

                int totalTime    = info.path("totalTime").asInt(0);
                int totalPayment = info.path("totalPayment").asInt(0);

                List<RootResult.DistanceStep> steps = new ArrayList<>();
                JsonNode subPaths = path.path("subPath");
                if (subPaths.isArray()) {
                    for (JsonNode sub : subPaths) {
                        int trafficType = sub.path("trafficType").asInt(); // 4=train, 5=고속, 6=시외, 7=항공
                        RootResult.TransportType t = mapTransportType(trafficType);

                        String lineInfo = null;
                        if (t == RootResult.TransportType.TRAIN) {
                            int trainType = sub.path("trainType").asInt(-1);
                            lineInfo = mapTrainType(trainType); // KTX/SRT/무궁화/ITX 등
                        } else if (t == RootResult.TransportType.BUS) {
                            lineInfo = (trafficType == 5) ? "고속버스" : "시외버스";
                        } else if (t == RootResult.TransportType.AIR) {
                            lineInfo = "항공";
                        }

                        steps.add(RootResult.DistanceStep.builder()
                                .type(t)
                                .time(sub.path("sectionTime").asInt(0))
                                .startName(sub.path("startName").asText(null))
                                .endName(sub.path("endName").asText(null))
                                .lineInfo(lineInfo)
                                .build());
                    }
                }


                routes.add(RootResult.builder()
                        .totalTime(totalTime)
                        .totalPayment(totalPayment)
                        .steps(steps)
                        .build());
            }
        }

        return InterCityResult.builder()
                .searchType(searchType)
                .busCount(busCount)
                .trainCount(trainCount)
                .airCount(airCount)
                .mixedCount(mixedCount)
                .routes(List.copyOf(routes))
                .build();
    }

    // =================
    //  내부 로직
    // =================

    private static RootResult.TransportType mapTransportType(int trafficType) {
        return switch (trafficType) {
            case 4 -> RootResult.TransportType.TRAIN;
            case 5, 6 -> RootResult.TransportType.BUS;
            case 7 -> RootResult.TransportType.AIR;
            default -> RootResult.TransportType.WALK; // 방어값
        };
    }

    /** 공급자 문서 기준 매핑 (필요 시 확장/보정) */
    private static String mapTrainType(int trainType) {
        return switch (trainType) {
            case 1 -> "KTX";
            case 2 -> "새마을";
            case 3 -> "무궁화";
            case 4 -> "누리로";
            case 5 -> "통근";
            case 6 -> "ITX";
            case 7 -> "ITX-청춘";
            case 8 -> "SRT";
            default -> "TRAIN(" + trainType + ")";
        };
    }
}
