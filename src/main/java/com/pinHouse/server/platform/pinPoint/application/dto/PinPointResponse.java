package com.pinHouse.server.platform.pinPoint.application.dto;

import com.pinHouse.server.platform.pinPoint.domain.entity.PinPoint;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;

@Schema(name = "[응답][핀포인트] 핀포인트 응답", description = "핀포인트 응답을 위한 DTO입니다.")
@Builder
public record PinPointResponse(
        @Schema(description = "저장한 이름", example = "회사 본사")
        String name,

        @Schema(description = "주소명", example = "서울특별시 강남구 테헤란로 123")
        String address,

        @Schema(description = "경도", example = "127.027637")
        double longitude,

        @Schema(description = "위도", example = "37.497942")
        double latitude,

        @Schema(description = "우선순위 여부", example = "true")
        boolean isFirst
) {

    /// 정적 팩토리 메서드
    public static PinPointResponse from(PinPoint pinPoint) {
        return PinPointResponse.builder()
                .name(pinPoint.getName())
                .address(pinPoint.getAddress())
                .longitude(pinPoint.getLongitude())
                .latitude(pinPoint.getLatitude())
                .isFirst(pinPoint.isFirst())
                .build();
    }

    /// 정적 팩토리 메서드 (List)
    public static List<PinPointResponse> from(List<PinPoint> pinPoints){
        return pinPoints.stream()
                .map(PinPointResponse::from)
                .toList();
    }

}
