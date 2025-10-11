package com.pinHouse.server.platform.housing.complex.application.dto;

import lombok.Builder;

@Builder
public record LocationResponse(
        double longitude,                   // 위도
        double latitude                    // 경도
) {

    /// 정적 팩토리 메서드
    public static LocationResponse from(double longitude, double latitude) {
        return LocationResponse.builder()
                .longitude(longitude)
                .latitude(latitude)
                .build();
    }
}
