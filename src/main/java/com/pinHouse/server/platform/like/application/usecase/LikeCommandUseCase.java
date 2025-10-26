package com.pinHouse.server.platform.like.application.usecase;

import com.pinHouse.server.platform.like.application.dto.LikeRequest;

import java.util.UUID;

public interface LikeCommandUseCase {

    // =================
    //  퍼블릭 로직
    // =================

    /// 좋아요 저장
    void saveLike(UUID userId, LikeRequest request);

    /// 좋아요 취소
    void deleteLike(Long id, UUID userId);

    // =================
    //  외부 로직
    // =================

}
