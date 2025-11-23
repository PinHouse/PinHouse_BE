package com.pinHouse.server.platform.search.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * 검색 키워드 통계 엔티티
 * 사용자가 검색한 키워드를 추적하고 인기 검색어를 제공하기 위한 컬렉션
 */
@Getter
@NoArgsConstructor
@Document(collection = "search_keywords")
public class SearchKeyword {

    @Id
    private String id;

    /**
     * 검색 키워드 (정규화됨: 소문자, 공백 제거)
     */
    @Indexed(unique = true)
    private String keyword;

    /**
     * 검색 횟수
     */
    @Indexed
    private Long count;

    /**
     * 마지막 검색 시각
     */
    @Indexed
    private Instant lastSearchedAt;

    /**
     * 최초 검색 시각
     */
    private Instant firstSearchedAt;

    @Builder
    public SearchKeyword(String keyword, Long count, Instant lastSearchedAt, Instant firstSearchedAt) {
        this.keyword = keyword;
        this.count = count;
        this.lastSearchedAt = lastSearchedAt;
        this.firstSearchedAt = firstSearchedAt;
    }

    /**
     * 새로운 검색 키워드 생성
     */
    public static SearchKeyword create(String keyword) {
        Instant now = Instant.now();
        return SearchKeyword.builder()
                .keyword(normalizeKeyword(keyword))
                .count(1L)
                .lastSearchedAt(now)
                .firstSearchedAt(now)
                .build();
    }

    /**
     * 검색 횟수 증가
     */
    public void incrementCount() {
        this.count++;
        this.lastSearchedAt = Instant.now();
    }

    /**
     * 키워드 정규화 (소문자 변환, 공백 제거)
     */
    public static String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return "";
        }
        return keyword.trim().toLowerCase();
    }
}
