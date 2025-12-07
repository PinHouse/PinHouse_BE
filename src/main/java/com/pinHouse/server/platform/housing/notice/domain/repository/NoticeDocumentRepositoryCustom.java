package com.pinHouse.server.platform.housing.notice.domain.repository;

import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListRequest;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.Instant;

public interface NoticeDocumentRepositoryCustom {

    Page<NoticeDocument> findNoticesByFilters(NoticeListRequest request, Pageable pageable, Instant now);

    /**
     * 텍스트 검색을 통한 공고 검색 (Page 방식 - deprecated)
     * MongoDB의 text index를 사용하여 제목 기반 검색
     *
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @param filterOpen true면 모집중만, false면 전체
     * @param now 현재 시각
     * @return 검색 결과
     */
    Page<NoticeDocument> searchByTitle(String keyword, Pageable pageable, boolean filterOpen, Instant now);

    /**
     * 텍스트 검색을 통한 공고 검색 (Slice 방식 - 무한 스크롤)
     * MongoDB의 regex를 사용하여 제목 기반 검색
     *
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @param filterOpen true면 모집중만, false면 전체
     * @param now 현재 시각
     * @return 검색 결과 (무한 스크롤)
     */
    Slice<NoticeDocument> searchByTitleSlice(String keyword, Pageable pageable, boolean filterOpen, Instant now);

}
