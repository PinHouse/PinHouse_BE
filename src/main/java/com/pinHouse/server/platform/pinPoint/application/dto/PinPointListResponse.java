package com.pinHouse.server.platform.pinPoint.application.dto;

import com.pinHouse.server.platform.pinPoint.domain.entity.PinPoint;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;

@Schema(name = "[응답][핀포인트] 핀포인트 목록 응답", description = "핀포인트 목록 조회를 위한 DTO입니다.")
@Builder
public record PinPointListResponse(

        @Schema(description = "유저 이름", example = "홍길동")
        String userName,

        @Schema(description = "핀포인트 목록")
        List<PinPointResponse> pinPoints

) {

    /// 정적 팩토리 메서드
    public static PinPointListResponse of(String userName, List<PinPoint> pinPoints) {

        return PinPointListResponse.builder()
                .userName(userName)
                .pinPoints(PinPointResponse.from(pinPoints))
                .build();
    }

}
