package com.pinHouse.server.platform.home.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "[요청][홈] 핀포인트 기준 공고 개수 조회", description = "핀포인트 기준 최대 이동 시간 내 공고 개수 조회 요청입니다.")
public record NoticeCountRequest(

        @NotBlank(message = "pinPointId는 필수 입력값입니다")
        @Schema(description = "핀포인트 ID (기준 위치)", example = "fec9aba3-0fd9-4b75-bebf-9cb7641fd251", required = true)
        String pinPointId,

        @Min(value = 1, message = "maxTime은 1분 이상이어야 합니다")
        @Schema(description = "대중교통 최대 소요 시간 (분)", example = "30", required = true)
        int maxTime

) {
}
