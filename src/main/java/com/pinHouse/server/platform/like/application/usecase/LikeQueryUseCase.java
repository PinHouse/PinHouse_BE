package com.pinHouse.server.platform.like.application.usecase;

import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.platform.like.application.dto.ComplexLikeResponse;

import java.util.List;
import java.util.UUID;

public interface LikeQueryUseCase {

    // =================
    //  퍼블릭 로직
    // =================

    /// 나의 좋아요 공고 목록 조회
    List<NoticeListResponse> getNoticesLike(UUID userId);

    /// 나의 좋아요 방 목록 조회
    List<ComplexLikeResponse> getComplexesLikes(UUID userId);


}
