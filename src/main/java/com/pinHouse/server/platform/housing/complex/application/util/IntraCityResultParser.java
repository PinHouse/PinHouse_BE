package com.pinHouse.server.platform.housing.complex.application.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.pinHouse.server.platform.housing.complex.application.dto.result.RootResult;
import com.pinHouse.server.platform.housing.complex.application.dto.result.IntraCityResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IntraCityResultParser {

    // =================
    //  퍼블릭 로직
    // =================

    /// 도시 내부 길찾기
    public static IntraCityResult parse(JsonNode root) {
        JsonNode result = root.path("result");

        /// 기본 값 추출
        int searchType       = result.path("searchType").asInt(0); // 0-도시내
        int busCount         = result.path("busCount").asInt(0);
        int subwayCount      = result.path("subwayCount").asInt(0);
        int subwayBusCount   = result.path("subwayBusCount").asInt(0);
        double pointDistance = result.path("pointDistance").asDouble(0d);

        /// 루트 생성
        List<RootResult> routes = new ArrayList<>();
        JsonNode paths = result.path("path");

        /// 길 찾기 내용 추출
        if (paths.isArray()) {
            for (JsonNode pathNode : paths) {
                JsonNode info = pathNode.path("info");

                /// 기본 값 추출
                int totalTime          = info.path("totalTime").asInt(0);
                int totalPayment       = info.path("payment").asInt(0);

                List<RootResult.DistanceStep> steps = new ArrayList<>();
                JsonNode subPaths = pathNode.path("subPath");

                /// 세부 길 찾기
                if (subPaths.isArray()) {
                    for (JsonNode sub : subPaths) {
                        int tt = sub.path("trafficType").asInt(); // 1-지하철, 2-버스, 3-도보
                        RootResult.TransportType type = switch (tt) {
                            case 1 -> RootResult.TransportType.SUBWAY;
                            case 2 -> RootResult.TransportType.BUS;
                            default -> RootResult.TransportType.WALK;
                        };

                        String lineInfo = null;
                        JsonNode lane = sub.path("lane");
                        if (lane.isArray() && !lane.isEmpty()) {
                            if (type == RootResult.TransportType.SUBWAY) {
                                lineInfo = joinField(lane, "name");
                                if (lineInfo != null
                                        && !lineInfo.endsWith("호선")
                                        && lineInfo.chars().allMatch(ch -> Character.isDigit(ch) || ch == ',' || ch == ' ')) {
                                    lineInfo = addSuffixForEachNumber(lineInfo, "호선");
                                }
                            } else if (type == RootResult.TransportType.BUS) {
                                lineInfo = joinField(lane, "busNo");
                            }
                        }

                        steps.add(RootResult.DistanceStep.builder()
                                .type(type)
                                .time(sub.path("sectionTime").asInt(0))
                                .startName(safeText(sub, "startName"))
                                .endName(safeText(sub, "endName"))
                                .lineInfo(lineInfo)
                                .build());
                    }
                }

                routes.add(RootResult.builder()
                        .totalTime(totalTime)
                        .totalPayment(totalPayment)
                        .steps(List.copyOf(steps))
                        .build());
            }
        }

        return IntraCityResult.builder()
                .searchType(searchType)
                .busCount(busCount)
                .subwayCount(subwayCount)
                .subwayBusCount(subwayBusCount)
                .pointDistance(pointDistance)
                .routes(List.copyOf(routes))
                .build();
    }

    // =================
    //  내부 로직
    // =================

    private static String joinField(JsonNode arrayNode, String field) {
        var list = new ArrayList<String>();
        arrayNode.forEach(n -> {
            String v = n.path(field).asText(null);
            if (v != null && !v.isBlank()) list.add(v);
        });
        if (list.isEmpty()) return null;
        return list.stream().filter(Objects::nonNull).distinct().collect(Collectors.joining(", "));
    }

    private static String addSuffixForEachNumber(String text, String suffix) {
        var parts = text.split(",");
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i].trim();
            if (p.chars().allMatch(Character::isDigit)) parts[i] = p + suffix;
            else parts[i] = p;
        }
        return String.join(", ", parts);
    }

    private static String safeText(JsonNode node, String field) {
        if (node == null || !node.has(field)) return null;
        String v = node.path(field).asText(null);
        return (v == null || v.isBlank()) ? null : v;
    }
}
