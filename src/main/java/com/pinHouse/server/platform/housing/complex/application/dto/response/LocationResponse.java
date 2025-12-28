package com.pinHouse.server.platform.housing.complex.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "[응답][위치] 위치 좌표 정보", description = "위도와 경도 좌표를 응답하는 DTO")
public record LocationResponse(
        @Schema(description = "경도 (Longitude)", example = "127.0276")
        double longitude,

        @Schema(description = "위도 (Latitude)", example = "37.4979")
        double latitude
) {

    /// 정적 팩토리 메서드
    public static LocationResponse from(double longitude, double latitude) {
        return LocationResponse.builder()
                .longitude(longitude)
                .latitude(latitude)
                .build();
    }
}
