package com.pinHouse.server.platform.like.application.usecase;

import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.platform.like.application.dto.ComplexLikeResponse;
import com.pinHouse.server.platform.like.application.dto.LikeRequest;

import java.util.UUID;
import java.util.List;

public interface LikeUseCase {

    // =================
    //  퍼블릭 로직
    // =================

    /// 좋아요 저장
    void saveLike(UUID userId, LikeRequest request);

    /// 좋아요 취소
    void deleteLike(Long id, UUID userId);

    /// 나의 좋아요 공고 목록 조회
    List<NoticeListResponse> getNoticeLikes(UUID userId);

    /// 나의 좋아요 방 목록 조회
    List<ComplexLikeResponse> getComplexesLikes(UUID userId);

    // =================
    //  외부 로직
    // =================

}
