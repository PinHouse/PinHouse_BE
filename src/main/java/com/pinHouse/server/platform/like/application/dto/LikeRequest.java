package com.pinHouse.server.platform.like.application.dto;

import com.pinHouse.server.platform.like.domain.LikeType;

/// 좋아요 요청 DTO
public record LikeRequest(
        String targetId,
        LikeType type
) {
}
