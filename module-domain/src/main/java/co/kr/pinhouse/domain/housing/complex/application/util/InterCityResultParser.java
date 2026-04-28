package co.kr.pinhouse.domain.housing.complex.application.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

import co.kr.pinhouse.domain.housing.complex.domain.transit.BusRouteType;
import co.kr.pinhouse.domain.housing.complex.domain.transit.ExpressBusType;
import co.kr.pinhouse.domain.housing.complex.domain.transit.InterCityResult;
import co.kr.pinhouse.domain.housing.complex.domain.transit.LineInfo;
import co.kr.pinhouse.domain.housing.complex.domain.transit.RootResult;
import co.kr.pinhouse.domain.housing.complex.domain.transit.SubwayLineType;
import co.kr.pinhouse.domain.housing.complex.domain.transit.TrainType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
		int busCount = result.path("busCount").asInt(0);
		int trainCount = result.path("trainCount").asInt(0);
		int airCount = result.path("airCount").asInt(0);
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
						RootResult.TransportType transportType = RootResult.TransportType.fromTrafficType(trafficType);

						String lineInfo = null;
						SubwayLineType subwayLine = null;
						BusRouteType busRouteType = null;
						TrainType trainTypeEnum = null;
						ExpressBusType expressBusType = null;
						JsonNode lane = sub.path("lane");
						JsonNode firstLane =
							(lane.isArray() && !lane.isEmpty())
								? lane.get(0)
								: null;

						// 도시간 응답이라도 첫/마지막 구간은 지하철/시내버스인 경우가 많다.
						// 색상/라벨을 유지하려면 교통수단별 세부 메타데이터를 여기서 같이 채워야 한다.
						if (transportType == RootResult.TransportType.SUBWAY && firstLane != null) {
							lineInfo = joinField(lane, "name");
							if (lineInfo != null
								&& !lineInfo.endsWith("호선")
								&& lineInfo.chars()
								.allMatch(ch -> Character.isDigit(ch) || ch == ',' || ch == ' ')) {
								lineInfo = addSuffixForEachNumber(lineInfo, "호선");
							}
							subwayLine = SubwayLineType.from(safeText(firstLane, "subwayCode"));
						} else if (transportType == RootResult.TransportType.TRAIN) {
							int trainTypeCode = sub.path("trainType").asInt(-1);
							trainTypeEnum = TrainType.from(trainTypeCode);
							lineInfo =
								(trainTypeEnum != TrainType.UNKNOWN)
									? trainTypeEnum.getLabel()
									: joinField(lane, "name");
						} else if (transportType == RootResult.TransportType.BUS && firstLane != null) {
							if (trafficType == 5 || trafficType == 6) {
								String expressBusTypeStr = safeText(firstLane, "type");
								expressBusType = parseExpressBusType(expressBusTypeStr);
								lineInfo = (trafficType == 5) ? "고속버스" : "시외버스";
							} else {
								lineInfo = joinField(lane, "busNo");
								busRouteType = BusRouteType.from(safeText(firstLane, "type"));
							}
						} else if (transportType == RootResult.TransportType.AIR) {
							lineInfo = joinField(lane, "name");
							if (lineInfo == null) {
								lineInfo = "항공";
							}
						}

						// LineInfo 생성
						LineInfo line = null;
						if (subwayLine != null) {
							line = subwayLine.toLineInfo();
						} else if (busRouteType != null) {
							line = busRouteType.toLineInfo();
						} else if (expressBusType != null) {
							line = expressBusType.toLineInfo();
						} else if (trainTypeEnum != null) {
							line = trainTypeEnum.toLineInfo();
						}

						steps.add(RootResult.DistanceStep.builder()
							.type(transportType)
							.time(sub.path("sectionTime").asInt(0))
							.distance(sub.path("distance").asInt(0))
							.startName(sub.path("startName").asText(null))
							.endName(sub.path("endName").asText(null))
							.startX(sub.path("startX").asDouble(0))
							.startY(sub.path("startY").asDouble(0))
							.endX(sub.path("endX").asDouble(0))
							.endY(sub.path("endY").asDouble(0))
							.lineInfo(lineInfo)
							.line(line)
							.subwayLine(subwayLine)
							.busRouteType(busRouteType)
							.trainType(trainTypeEnum)
							.expressBusType(expressBusType)
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

	private static String joinField(JsonNode arrayNode, String field) {
		var list = new ArrayList<String>();
		arrayNode.forEach(node -> {
			String value = node.path(field).asText(null);
			if (value != null && !value.isBlank()) {
				list.add(value);
			}
		});
		if (list.isEmpty()) {
			return null;
		}
		return list.stream()
			.filter(Objects::nonNull)
			.distinct()
			.collect(Collectors.joining(", "));
	}

	private static String addSuffixForEachNumber(String text, String suffix) {
		var parts = text.split(",");
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i].trim();
			if (part.chars().allMatch(Character::isDigit)) {
				parts[i] = part + suffix;
			} else {
				parts[i] = part;
			}
		}
		return String.join(", ", parts);
	}

	private static ExpressBusType parseExpressBusType(String code) {
		if (code == null || code.isBlank()) {
			return ExpressBusType.UNKNOWN;
		}
		try {
			return ExpressBusType.from(Integer.parseInt(code.trim()));
		} catch (NumberFormatException e) {
			return ExpressBusType.UNKNOWN;
		}
	}

	private static String safeText(JsonNode node, String field) {
		if (node == null || !node.has(field)) {
			return null;
		}
		String value = node.path(field).asText(null);
		return (value == null || value.isBlank()) ? null : value;
	}
}
