package com.pinHouse.server.platform.housing.complex.application.dto.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 교통수단 노선 정보 통합 DTO
 * 지하철, 버스, 열차 등 모든 교통수단의 노선 정보를 단일 구조로 표현
 */
@Builder
@Schema(name = "교통수단 노선 정보", description = "교통수단의 코드, 이름, 색상 정보")
public record LineInfo(
        @Schema(description = "노선 코드", example = "1")
        Integer code,

        @Schema(description = "노선명", example = "KTX")
        String label,

        @Schema(description = "배경 색상 (Hex 코드)", example = "#3356B4")
        String bgColorHex
) {
    /**
     * null safe builder - 모든 필드가 null인 경우 null 반환
     */
    public static LineInfo of(Integer code, String label, String bgColorHex) {
        if (code == null && label == null && bgColorHex == null) {
            return null;
        }
        return new LineInfo(code, label, bgColorHex);
    }
}
