package co.kr.pinhouse.domain.housing.complex.domain.transit;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

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

		@Schema(description = "통합 노선 정보 (코드, 이름, 색상)")
		LineInfo line,

		// 아래 메타 필드는 응답 직렬화용이 아니라 색상/라벨 계산을 위한 내부 보조 데이터입니다.
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
		ExpressBusType expressBusType

	) {
	}
}
