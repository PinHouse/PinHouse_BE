package com.pinHouse.server.platform.housing.notice.application.usecase;

import com.pinHouse.server.core.response.response.pageable.PageRequest;
import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.platform.housing.notice.domain.entity.Notice;
import com.pinHouse.server.platform.search.application.dto.FastSearchRequest;

import java.util.List;

/**
 * [공고 기반 조회] 인터페이스
 * - 최신 기반, 공고의 목록 조회
 * - 지역 필터링 기반, 공고 목록 조회
 * - 특정 공고 ID를 바탕으로 상세 조회
 * - 특정 공고 내부의 예산 정보를 바탕으로 [예산 시뮬레이터] 구현
 * - 특정 공고의 ID를 바탕으로 비교
 */

public interface NoticeUseCase {

    // =================
    //  퍼블릭 로직
    // =================

    /// 최신 목록 조회
    SliceResponse<NoticeListResponse> getNotices(PageRequest sliceRequest);

    /// 지역 필터링 기반, 공고 목록 조회
    SliceResponse<NoticeListResponse> getNoticesByRegion(String region, PageRequest sliceRequest);

    /// 상세 조회
    NoticeDetailResponse getNoticeById(String noticeId);

    /// 비교
    List<Notice> compareNotices(String noticeId1, String noticeId2);

    // =================
    //  외부 로직
    // =================

    /// 상세 조회
    Notice loadNotice(String id);

    /// 모든 공고 가져오기
    List<Notice> loadAllNotices();

    /// 필터링을 위한 함수
    List<Notice> filterNotices(FastSearchRequest request);


}
