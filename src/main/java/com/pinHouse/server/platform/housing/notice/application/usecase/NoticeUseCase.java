package com.pinHouse.server.platform.housing.notice.application.usecase;

import com.pinHouse.server.core.response.response.pageable.SliceRequest;
import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.SortType;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import com.pinHouse.server.platform.search.application.dto.FastSearchRequest;

import java.util.List;

public interface NoticeUseCase {

    // =================
    //  퍼블릭 로직
    // =================

    /// 공고 목록 전체 조회
    SliceResponse<NoticeListResponse> getNotices(SortType sortType, SliceRequest sliceRequest);

    /// 공고 상세 조회
    NoticeDetailResponse getNotice(String noticeId);

    // =================
    //  외부 로직
    // =================

    /// 상세 조회
    NoticeDocument loadNotice(String id);

    /// 모든 공고 가져오기
    List<NoticeDocument> loadAllNotices();

    /// 필터링을 위한 함수
    List<NoticeDocument> filterNotices(FastSearchRequest request);

}
