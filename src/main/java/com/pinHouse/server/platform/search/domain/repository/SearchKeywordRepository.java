package com.pinHouse.server.platform.search.domain.repository;

import com.pinHouse.server.platform.search.domain.entity.SearchKeyword;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 검색 키워드 레포지토리
 */
@Repository
public interface SearchKeywordRepository extends MongoRepository<SearchKeyword, String> {

    /**
     * 키워드로 검색 키워드 조회
     */
    Optional<SearchKeyword> findByKeyword(String keyword);

    /**
     * 검색 횟수 기준으로 정렬하여 상위 N개 조회
     * @param pageable 페이징 정보 (정렬 포함)
     * @return 인기 검색어 목록
     */
    List<SearchKeyword> findAllByOrderByCountDescLastSearchedAtDesc(Pageable pageable);

    /**
     * 키워드가 특정 접두어로 시작하는 검색어 조회 (자동완성용)
     * @param prefix 검색어 접두어
     * @param pageable 페이징 정보
     * @return 매칭되는 검색어 목록
     */
    List<SearchKeyword> findByKeywordStartingWithOrderByCountDescLastSearchedAtDesc(String prefix, Pageable pageable);
}
