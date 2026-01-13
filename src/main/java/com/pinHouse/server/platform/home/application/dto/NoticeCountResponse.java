package com.pinHouse.server.platform.home.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "[응답][홈] 핀포인트 기준 공고 개수", description = "최대 이동 시간 내 공고 개수 응답입니다.")
@Builder
public record NoticeCountResponse(

        @Schema(description = "공고 개수", example = "15")
        long count

) {

    public static NoticeCountResponse from(long count) {
        return NoticeCountResponse.builder()
                .count(count)
                .build();
    }
}
