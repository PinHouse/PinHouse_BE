package com.pinHouse.server.platform.home.application.usecase;

import com.pinHouse.server.core.response.response.pageable.SliceRequest;
import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.home.application.dto.HomeNoticeListResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListRequest;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchFilterType;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchResultResponse;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchSortType;

import java.util.UUID;

/**
 * 홈 화면 Use Case
 */
public interface HomeUseCase {

    /**
     * 마감임박공고 조회 (PinPoint 지역 기반)
     * @param pinpointId PinPoint ID (해당 지역의 공고를 조회)
     * @param sliceRequest 페이징 정보
     * @param userId 사용자 ID (좋아요 정보 조회용, null 가능)
     * @return 해당 지역의 마감임박순으로 정렬된 공고 목록
     */
    SliceResponse<HomeNoticeListResponse> getDeadlineApproachingNotices(
            String pinpointId,
            SliceRequest sliceRequest,
            UUID userId
    );

    /**
     * 통합 검색 (공고 제목 및 타겟 그룹 기반)
     * @param keyword 검색 키워드
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지 크기
     * @param sortType 정렬 방식 (LATEST: 최신공고순, END: 마감임박순)
     * @param status 공고 상태 (ALL: 전체, RECRUITING: 모집중)
     * @param userId 사용자 ID (좋아요 정보 조회용, null 가능)
     * @return 검색 결과 (무한 스크롤 응답)
     */
    SliceResponse<NoticeSearchResultResponse> searchNoticesIntegrated(
            String keyword,
            int page,
            int size,
            NoticeSearchSortType sortType,
            NoticeSearchFilterType status,
            UUID userId
    );
}
