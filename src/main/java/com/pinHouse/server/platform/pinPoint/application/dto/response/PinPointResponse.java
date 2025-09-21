package com.pinHouse.server.platform.pinPoint.application.dto.response;

import com.pinHouse.server.platform.pinPoint.domain.entity.PinPoint;
import lombok.Builder;
import java.util.List;

/**
 * 핀포인트 응답 값
 * @param name      저장한 이름
 * @param address   주소명
 * @param isFirst   우선순위 여부
 */
@Builder
public record PinPointResponse(
        String name,
        String address,
        double longitude,
        double latitude,
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
