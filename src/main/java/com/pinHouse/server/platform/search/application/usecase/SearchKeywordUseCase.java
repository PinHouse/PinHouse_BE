package com.pinHouse.server.platform.search.application.usecase;

import com.pinHouse.server.platform.search.application.dto.PopularKeywordResponse;
import com.pinHouse.server.platform.search.application.dto.SearchSuggestionResponse;
import com.pinHouse.server.platform.search.domain.entity.SearchKeywordScope;

import java.util.List;

/**
 * 검색 키워드 관리 Use Case
 */
public interface SearchKeywordUseCase {

    /**
     * 검색 키워드 기록 (비동기 또는 동기)
     * @param keyword 검색 키워드
     */
    void recordSearch(String keyword);

    /**
     * 검색 키워드 기록 (스코프 구분)
     */
    void recordSearch(String keyword, SearchKeywordScope scope);

    /**
     * 인기 검색어 조회
     * @param limit 조회할 검색어 개수
     * @return 인기 검색어 목록
     */
    List<PopularKeywordResponse> getPopularKeywords(int limit);

    /**
     * 스코프별 인기 검색어 조회
     */
    List<PopularKeywordResponse> getPopularKeywords(int limit, SearchKeywordScope scope);
}
