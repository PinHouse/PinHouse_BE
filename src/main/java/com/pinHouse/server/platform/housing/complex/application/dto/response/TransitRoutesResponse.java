package com.pinHouse.server.platform.housing.complex.application.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pinHouse.server.platform.housing.complex.application.dto.result.LineInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

/**
 * 대중교통 경로 응답 (3개 경로 한 번에 제공)
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "[응답][대중교통] 전체 경로 응답", description = "3개의 대중교통 경로를 한 번에 제공하는 응답 DTO")
public record TransitRoutesResponse(

        @Schema(description = "전체 경로 개수", example = "3")
        int totalCount,

        @Schema(description = "경로 리스트 (최대 3개)")
        List<RouteResponse> routes
) {

    /**
     * 개별 경로 정보
     */
    @Builder
    @Schema(name = "[응답][대중교통] 개별 경로", description = "하나의 대중교통 경로 정보")
    public record RouteResponse(

            @Schema(description = "경로 인덱스 (0부터 시작)", example = "0")
            int routeIndex,

            @Schema(description = "경로 요약 정보")
            SummaryResponse summary,

            @Schema(description = "색 막대용 구간 정보 배열")
            List<SegmentResponse> distance,

            @Schema(description = "세부 경로 단계 배열 (색깔 + 승차/하차 + 소요시간 모두 포함)")
            List<StepResponse> steps
    ) {
    }

    /**
     * 경로 요약 정보
     */
    @Builder
    @Schema(name = "[응답][대중교통] 경로 요약", description = "총 시간, 거리, 요금, 환승 횟수")
    public record SummaryResponse(

            @Schema(description = "총 소요 시간(분)", example = "83")
            int totalMinutes,

            @Schema(description = "총 거리(km)", example = "39.6")
            double totalDistanceKm,

            @Schema(description = "총 요금(원), 없으면 null", example = "1350")
            Integer totalFareWon,

            @Schema(description = "환승 횟수", example = "1")
            int transferCount,

            @Schema(description = "UI 표시용 시간 텍스트", example = "1시간 23분")
            String displayText
    ) {
    }

    /**
     * 색 막대용 구간 정보
     */
    @Builder
    @Schema(name = "[응답][대중교통] 세그먼트", description = "색 막대 렌더링용 구간 정보")
    public record SegmentResponse(

            @Schema(description = "이동 수단 (WALK, BUS, SUBWAY, TRAIN, AIR)", example = "SUBWAY")
            String type,

            @Schema(description = "소요 시간(분)", example = "65")
            int minutes,

            @Schema(description = "막대 위 표시 텍스트 (호선명, 버스번호, 또는 소요시간), WALK인 경우 null", example = "수도권 7호선")
            String labelText,

            @Schema(description = "구간 색상(Hex)", example = "#3356B4")
            String colorHex,

            @Schema(description = "노선 정보 (WALK면 null)")
            LineInfo line
    ) {}

    /**
     * 세부 경로 단계 (색깔 + 승차/하차 + 시간 정보 통합)
     */
    @Builder
    @Schema(name = "[응답][대중교통] 단계", description = "세부 경로 단계 (색깔 + 승차/하차/도보 등 모든 정보 포함)")
    public record StepResponse(

            @Schema(description = "단계 인덱스 (0부터 시작)", example = "0")
            int stepIndex,

            @Schema(description = "행동 타입 (DEPART, WALK, BOARD, ALIGHT, ARRIVE)", example = "BOARD")
            StepAction action,

            @Schema(description = "이동 수단 (WALK, BUS, SUBWAY, TRAIN, AIR, null)", example = "SUBWAY")
            String type,

            @Schema(description = "정류장/역 이름", example = "시청역")
            String stopName,

            @Schema(description = "주 텍스트 (UI에 굵게 표시)", example = "시청역 승차")
            String primaryText,

            @Schema(description = "부 텍스트 (노선명, 버스번호 등)", example = "수도권 1호선")
            String secondaryText,

            @Schema(description = "해당 구간 소요 시간(분), 없으면 null", example = "65")
            Integer minutes,

            @Schema(description = "색 막대용 색상(Hex), 출발/도착은 null", example = "#3356B4")
            String colorHex,

            @Schema(description = "노선 정보 (WALK면 null)")
            LineInfo line
    ) {
    }

    /**
     * 단계 행동 타입
     */
    public enum StepAction {
        DEPART,    // 출발
        WALK,      // 도보
        BOARD,     // 승차
        ALIGHT,    // 하차
        ARRIVE     // 도착
    }
}
