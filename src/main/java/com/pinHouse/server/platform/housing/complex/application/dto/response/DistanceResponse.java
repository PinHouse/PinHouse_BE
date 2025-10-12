package com.pinHouse.server.platform.housing.complex.application.dto.response;

import com.pinHouse.server.platform.housing.complex.application.dto.result.RootResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;

@Builder
public record DistanceResponse(

        @Schema(description = "총 소요 시간(분)", example = "45")
        int totalTime,

        @Schema(description = "총 거리 (KM)", example = "17")
        int totalDistance,

        List<TransitResponse> routes
) {

    /// 정적 팩토리 메서드
    public static DistanceResponse from(RootResult rootResult, List<TransitResponse> routes) {
        return DistanceResponse.builder()
                .totalTime(rootResult.totalTime())
                .totalDistance(0)
                .routes(routes)
                .build();
    }





    @Schema(name = "[응답][교통] 세그먼트 구간 정보 Response", description = "제일 빠른 교통수단의 소요시간, 노선, 배경색 정보를 포함한 응답 DTO입니다.")
    @Builder
    public record TransitResponse(

            @Schema(description = "교통 타입 (WALK, BUS, SUBWAY, TRAIN, AIR)", example = "BUS")
            ChipType type,

            @Schema(description = "구간 소요 시간 텍스트", example = "12분")
            String minutesText,

            @Schema(description = "노선 정보(버스번호/지하철 호선 등), 없는 경우 null", example = "9401, G8110")
            String lineText,

            @Schema(description = "세그먼트 배경 컬러(Hex 코드)", example = "#FF5722")
            String bgColorHex)
    {
        /// 생성자
        public static TransitResponse from(ChipType type, String minutesText, String lineText, String bgColorHex, String iconName) {
            return TransitResponse.builder()
                    .type(type)
                    .minutesText(minutesText)
                    .lineText(lineText)
                    .bgColorHex(bgColorHex)
                    .build();

        }
    }
}
