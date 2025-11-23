package com.pinHouse.server.platform.search.infrastructure.config;

import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import com.pinHouse.server.platform.search.domain.entity.SearchKeyword;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;

/**
 * MongoDB 검색 인덱스 설정
 * 애플리케이션 시작 시 필요한 인덱스를 생성합니다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SearchMongoIndexConfig {

    private final MongoTemplate mongoTemplate;

    /**
     * 애플리케이션 시작 시 인덱스 생성
     */
    @PostConstruct
    public void initIndexes() {
        createNoticeTextIndex();
        createSearchKeywordIndexes();
        log.info("MongoDB search indexes initialized successfully");
    }

    /**
     * NoticeDocument의 title 필드에 text index 생성
     * 한국어 검색을 위해 언어 설정 없이 생성 (default)
     */
    private void createNoticeTextIndex() {
        try {
            TextIndexDefinition textIndex = TextIndexDefinition.builder()
                    .onField("title")  // 공고 제목 필드
                    .build();

            mongoTemplate.indexOps(NoticeDocument.class)
                    .ensureIndex(textIndex);

            log.info("Created text index on NoticeDocument.title");
        } catch (Exception e) {
            log.error("Failed to create text index on NoticeDocument.title", e);
        }
    }

    /**
     * SearchKeyword 컬렉션의 인덱스 생성
     * 1. keyword 필드에 unique index (중복 방지)
     * 2. count 필드에 index (인기 검색어 조회용)
     * 3. lastSearchedAt 필드에 index (최신 검색어 조회용)
     */
    private void createSearchKeywordIndexes() {
        try {
            // keyword 필드에 unique index (이미 @Indexed(unique=true) 어노테이션이 있지만 명시적으로 생성)
            Index keywordIndex = new Index()
                    .named("idx_keyword")
                    .on("keyword", Sort.Direction.ASC)
                    .unique();

            mongoTemplate.indexOps(SearchKeyword.class)
                    .ensureIndex(keywordIndex);

            // count 필드에 descending index (인기 검색어 조회 성능 향상)
            Index countIndex = new Index()
                    .named("idx_count")
                    .on("count", Sort.Direction.DESC);

            mongoTemplate.indexOps(SearchKeyword.class)
                    .ensureIndex(countIndex);

            // lastSearchedAt 필드에 descending index
            Index lastSearchedIndex = new Index()
                    .named("idx_last_searched")
                    .on("lastSearchedAt", Sort.Direction.DESC);

            mongoTemplate.indexOps(SearchKeyword.class)
                    .ensureIndex(lastSearchedIndex);

            // 복합 인덱스: count(desc) + lastSearchedAt(desc) - 인기 검색어 조회 최적화
            Index compoundIndex = new Index()
                    .named("idx_popular")
                    .on("count", Sort.Direction.DESC)
                    .on("lastSearchedAt", Sort.Direction.DESC);

            mongoTemplate.indexOps(SearchKeyword.class)
                    .ensureIndex(compoundIndex);

            log.info("Created indexes on SearchKeyword collection");
        } catch (Exception e) {
            log.error("Failed to create indexes on SearchKeyword collection", e);
        }
    }
}
