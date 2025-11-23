package com.pinHouse.server.platform.search.application.service;

import com.pinHouse.server.platform.search.application.dto.PopularKeywordResponse;
import com.pinHouse.server.platform.search.application.dto.SearchSuggestionResponse;
import com.pinHouse.server.platform.search.application.usecase.SearchKeywordUseCase;
import com.pinHouse.server.platform.search.domain.entity.SearchKeyword;
import com.pinHouse.server.platform.search.domain.repository.SearchKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * 검색 키워드 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchKeywordService implements SearchKeywordUseCase {

    private final SearchKeywordRepository repository;
    private final MongoTemplate mongoTemplate;

    /**
     * 검색 키워드 기록 (atomic upsert 사용)
     * MongoDB의 원자적 연산을 사용하여 동시성 문제 해결
     */
    @Override
    @Transactional
    public void recordSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        // 키워드 정규화 (소문자, 공백 제거)
        String normalizedKeyword = SearchKeyword.normalizeKeyword(keyword);

        if (normalizedKeyword.isEmpty()) {
            return;
        }

        try {
            // MongoDB atomic upsert를 사용한 카운트 증가
            Query query = new Query(Criteria.where("keyword").is(normalizedKeyword));
            Update update = new Update()
                    .inc("count", 1)
                    .set("lastSearchedAt", Instant.now())
                    .setOnInsert("firstSearchedAt", Instant.now());

            mongoTemplate.upsert(query, update, SearchKeyword.class);

            log.debug("Recorded search keyword: {}", normalizedKeyword);
        } catch (Exception e) {
            log.error("Failed to record search keyword: {}", normalizedKeyword, e);
            // 키워드 기록 실패는 검색 자체를 방해하지 않음
        }
    }

    /**
     * 인기 검색어 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<PopularKeywordResponse> getPopularKeywords(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<SearchKeyword> keywords = repository.findAllByOrderByCountDescLastSearchedAtDesc(pageable);
        return PopularKeywordResponse.from(keywords);
    }

    /**
     * 검색어 자동완성 제안
     */
    @Override
    @Transactional(readOnly = true)
    public SearchSuggestionResponse getSuggestions(String prefix, int limit) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return SearchSuggestionResponse.builder()
                    .suggestions(List.of())
                    .build();
        }

        String normalizedPrefix = SearchKeyword.normalizeKeyword(prefix);
        Pageable pageable = PageRequest.of(0, limit);

        List<SearchKeyword> keywords = repository
                .findByKeywordStartingWithOrderByCountDescLastSearchedAtDesc(normalizedPrefix, pageable);

        return SearchSuggestionResponse.from(keywords);
    }
}
