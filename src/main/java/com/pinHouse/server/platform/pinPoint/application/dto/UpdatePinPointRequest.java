package com.pinHouse.server.platform.pinPoint.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "[요청][핀포인트] 핀포인트 이름 수정", description = "사용자가 등록한 핀포인트의 이름을 수정하는 요청 DTO")
public record UpdatePinPointRequest(
        @Schema(description = "변경할 핀포인트 이름 (최대 20자)", example = "서울역 근처 집")
        String name
) {
}
