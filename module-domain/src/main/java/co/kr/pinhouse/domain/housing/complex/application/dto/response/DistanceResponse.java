package co.kr.pinhouse.domain.housing.complex.application.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import co.kr.pinhouse.common.util.TimeFormatter;
import co.kr.pinhouse.domain.housing.complex.domain.transit.BusRouteType;
import co.kr.pinhouse.domain.housing.complex.domain.transit.ExpressBusType;
import co.kr.pinhouse.domain.housing.complex.domain.transit.LineInfo;
import co.kr.pinhouse.domain.housing.complex.domain.transit.RootResult;
import co.kr.pinhouse.domain.housing.complex.domain.transit.SubwayLineType;
import co.kr.pinhouse.domain.housing.complex.domain.transit.TrainType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 공고/비교 서비스에서 아직 사용하는 구 교통 응답 DTO.
 * 외부 API 응답용이라기보다 캐시된 RootResult에서 총 시간과 레거시 chip 정보를 재조립하는 용도에 가깝다.
 */
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

	/// NoticeService 등 기존 호출부가 쓰는 경량 응답으로 변환
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

		@Schema(description = "막대 위 표시 텍스트 (호선명, 버스번호, 또는 소요시간), WALK인 경우 null", example = "수도권 7호선")
		String labelText,

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
		String bgColorHex) {
	}

}
