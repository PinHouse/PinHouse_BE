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

    /**
     * 텍스트 검색 결과의 총 개수 조회
     * MongoDB의 regex를 사용하여 제목 기반 검색
     *
     * @param keyword 검색 키워드
     * @param filterOpen true면 모집중만, false면 전체
     * @param now 현재 시각
     * @return 검색 결과 총 개수
     */
    long countByTitle(String keyword, boolean filterOpen, Instant now);

    /**
     * 지역(Region)과 시/군/구(County) 기반 마감임박 공고 조회
     * HomeService의 마감임박 공고 조회 전용 메서드
     *
     * @param region 광역 단위 (예: "경기도")
     * @param county 시/군/구 (예: "성남시", null 가능)
     * @param pageable 페이징 정보
     * @param now 현재 시각
     * @return 마감임박순으로 정렬된 모집중인 공고 목록
     */
    Page<NoticeDocument> findDeadlineApproachingNoticesByRegionAndCounty(
            String region,
            String county,
            Pageable pageable,
            Instant now
    );

    /**
     * 진단 결과 기반 추천 공고 조회
     * 사용자의 청약 진단 결과를 바탕으로 신청 가능한 공고를 조회
     *
     * @param supplyTypes 공급 유형 리스트 (진단 결과에서 매핑된 값)
     * @param pageable 페이징 및 정렬 정보 (마감임박순 권장)
     * @return 추천 공고 목록
     */
    Page<NoticeDocument> findRecommendedNoticesByDiagnosis(
            java.util.List<String> supplyTypes,
            Pageable pageable
    );

    /**
     * 모집대상 텍스트 검색 (중복 제거)
     */
    org.springframework.data.domain.Slice<String> searchTargetGroups(String keyword, Pageable pageable);

    /**
     * 지역 텍스트 검색 (도시/시군구 조합, 중복 제거)
     */
    org.springframework.data.domain.Slice<String> searchRegions(String keyword, Pageable pageable);

    /**
     * 주택유형 텍스트 검색 (중복 제거)
     */
    org.springframework.data.domain.Slice<String> searchHouseTypes(String keyword, Pageable pageable);

    /**
     * 모집대상 공고 검색 (Slice)
     */
    org.springframework.data.domain.Slice<NoticeDocument> searchNoticesByTargetGroup(String keyword, Pageable pageable);

    /**
     * 지역 공고 검색 (Slice)
     */
    org.springframework.data.domain.Slice<NoticeDocument> searchNoticesByRegion(String keyword, Pageable pageable);

    /**
     * 주택유형 공고 검색 (Slice)
     */
    org.springframework.data.domain.Slice<NoticeDocument> searchNoticesByHouseType(String keyword, Pageable pageable);

}
