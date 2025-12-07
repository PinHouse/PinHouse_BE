package com.pinHouse.server.platform.search.application.usecase;

import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchFilterType;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchResultResponse;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchSortType;

import java.util.UUID;

/**
 * 공고 검색 Use Case
 */
public interface NoticeSearchUseCase {

    /**
     * 공고 제목 기반 검색 (무한 스크롤)
     * @param keyword 검색 키워드
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지 크기
     * @param sortType 정렬 방식 (LATEST: 최신공고순, END: 마감임박순)
     * @param status 공고 상태 (ALL: 전체, RECRUITING: 모집중)
     * @param userId 사용자 ID (좋아요 정보 조회용, null 가능)
     * @return 검색 결과 (무한 스크롤 응답)
     */
    SliceResponse<NoticeSearchResultResponse> searchNotices(String keyword, int page, int size, NoticeSearchSortType sortType, NoticeSearchFilterType status, UUID userId);
}
