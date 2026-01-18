package com.pinHouse.server.platform.housing.complex.domain.repository;

import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.notice.application.dto.UnitTypeSortType;

import java.util.List;

/**
 * ComplexDocument 커스텀 Repository
 * MongoDB Aggregation을 사용한 복잡한 쿼리 처리
 */
public interface CustomComplexDocumentRepository {

    /**
     * 공고에 속한 모든 단지와 유닛타입을 정렬하여 조회
     *
     * @param noticeId 공고 ID
     * @param sortType 정렬 기준
     * @return 정렬된 단지 목록 (각 단지의 unitTypes도 정렬됨)
     */
    List<ComplexDocument> findSortedComplexesWithUnitTypes(String noticeId, UnitTypeSortType sortType);

    /**
     * 단지명 텍스트 검색 (무한 스크롤)
     *
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 단지 결과 슬라이스
     */
    org.springframework.data.domain.Slice<ComplexDocument> searchByName(String keyword, org.springframework.data.domain.Pageable pageable);
}
