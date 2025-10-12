package com.pinHouse.server.platform.pinPoint.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UpdatePinPointRequest", description = "핀 포인트 이름 업데이트 요청 DTO입니다.")
public record UpdatePinPointRequest(
        @Schema(description = "핀 포인트 이름", example = "서울역")
        String name
) {
}
