package com.pinHouse.server.platform.pinPoint.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 핀포인트 요청 DTO
 */
@Schema(name = "[요청][핀포인트] 핀포인트 요청", description = "핀포인트 요청을 위한 DTO입니다.")
public record PinPointRequest(

        @Schema(description = "핀포인트 주소", example = "서울특별시 강남구 테헤란로 123")
        String address,

        @Schema(description = "핀포인트 이름", example = "회사 본사")
        String name,

        @Schema(description = "우선순위 설정 여부, true인 경우 최우선")
        boolean first

) {



}
