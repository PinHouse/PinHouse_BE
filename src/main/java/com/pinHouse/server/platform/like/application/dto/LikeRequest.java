package com.pinHouse.server.platform.like.application.dto;

import com.pinHouse.server.platform.like.domain.LikeType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "[요청][좋아요] 좋아요 등록/취소", description = "공고 또는 유닛타입에 대한 좋아요를 등록하거나 취소하는 요청 DTO")
public record LikeRequest(
        @Schema(description = "좋아요 대상 ID (공고 ID 또는 유닛타입 ID)", example = "19417 또는 type456", required = true)
        String targetId,

        @Schema(description = "좋아요 타입 (NOTICE: 공고, ROOM: 유닛타입)", example = "NOTICE", required = true, allowableValues = {"NOTICE", "ROOM"})
        LikeType type
) {
}
