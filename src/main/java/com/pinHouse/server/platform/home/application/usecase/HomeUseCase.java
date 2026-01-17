package com.pinHouse.server.platform.home.application.usecase;

import com.pinHouse.server.core.response.response.pageable.SliceRequest;
import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.home.application.dto.HomeNoticeListResponse;
import com.pinHouse.server.platform.home.application.dto.NoticeCountResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListRequest;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchFilterType;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchResultResponse;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchSortType;

import java.util.UUID;

/**
 * 홈 화면 Use Case
 */
public interface HomeUseCase {

    /**
     * 마감임박공고 조회 (PinPoint 지역 기반)
     * @param pinpointId PinPoint ID (해당 지역의 공고를 조회)
     * @param sliceRequest 페이징 정보
     * @param userId 사용자 ID (좋아요 정보 조회용, null 가능)
     * @return 해당 지역의 마감임박순으로 정렬된 공고 목록 (region + notices 배열)
     */
    HomeNoticeListResponse getDeadlineApproachingNotices(
            String pinpointId,
            SliceRequest sliceRequest,
            UUID userId
    );

    /**
     * 통합 검색 (공고 제목 및 타겟 그룹 기반)
     * @param keyword 검색 키워드
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지 크기
     * @param sortType 정렬 방식 (LATEST: 최신공고순, END: 마감임박순)
     * @param status 공고 상태 (ALL: 전체, RECRUITING: 모집중)
     * @param userId 사용자 ID (좋아요 정보 조회용, null 가능)
     * @return 검색 결과 (무한 스크롤 응답)
     */
    SliceResponse<NoticeSearchResultResponse> searchNoticesIntegrated(
            String keyword,
            int page,
            int size,
            NoticeSearchSortType sortType,
            NoticeSearchFilterType status,
            UUID userId
    );

    /**
     * 핀포인트 기준 최대 이동 시간 내 공고 개수 조회
     * @param pinPointId 핀포인트 ID (기준 위치)
     * @param maxTime 최대 이동 시간 (분)
     * @param userId 사용자 ID (PinPoint 소유권 검증용)
     * @return 공고 개수
     */
    NoticeCountResponse getNoticeCountWithinTravelTime(String pinPointId, int maxTime, UUID userId);

    /**
     * 진단 기반 추천 공고 조회
     * 사용자의 최근 청약 진단 결과를 기반으로 맞춤형 공고를 추천
     *
     * @param sliceRequest 페이징 정보 (page, offSet)
     * @param userId 사용자 ID (진단 결과 조회 및 좋아요 정보용)
     * @return 진단 기반 추천 공고 목록 (마감임박순 정렬, 모든 공고 상태 포함)
     */
    HomeNoticeListResponse getRecommendedNoticesByDiagnosis(
            SliceRequest sliceRequest,
            UUID userId
    );
}
