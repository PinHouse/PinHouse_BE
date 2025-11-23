package com.pinHouse.server.platform.search.application.usecase;

import com.pinHouse.server.platform.search.application.dto.NoticeSearchResponse;

import java.util.UUID;

/**
 * 공고 검색 Use Case
 */
public interface NoticeSearchUseCase {

    /**
     * 공고 제목 기반 검색
     * @param keyword 검색 키워드
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param sort 정렬 방식 (LATEST: 최신순, DEADLINE: 마감임박순)
     * @param filter 필터 (OPEN: 모집중, ALL: 전체)
     * @param userId 사용자 ID (좋아요 정보 조회용, null 가능)
     * @return 검색 결과
     */
    NoticeSearchResponse searchNotices(String keyword, int page, int size, String sort, String filter, UUID userId);
}
