package com.pinHouse.server.platform.search.application.service;

import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import com.pinHouse.server.platform.housing.notice.domain.repository.NoticeDocumentRepository;
import com.pinHouse.server.platform.like.application.usecase.LikeQueryUseCase;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchFilterType;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchResultResponse;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchSortType;
import com.pinHouse.server.platform.search.application.usecase.NoticeSearchUseCase;
import com.pinHouse.server.platform.search.application.usecase.SearchKeywordUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 공고 검색 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeSearchService implements NoticeSearchUseCase {

    private final NoticeDocumentRepository noticeRepository;
    private final SearchKeywordUseCase searchKeywordService;
    private final LikeQueryUseCase likeService;

    /**
     * 공고 제목 기반 검색 (무한 스크롤)
     */
    @Override
    @Transactional(readOnly = true)
    public SliceResponse<NoticeSearchResultResponse> searchNotices(String keyword, int page, int size, NoticeSearchSortType sortType, NoticeSearchFilterType status, UUID userId) {
        // 검색 키워드 기록 (비동기로 처리 가능)
        if (keyword != null && !keyword.trim().isEmpty()) {
            searchKeywordService.recordSearch(keyword);
        }

        // 정렬 기준 설정 (null이면 기본값)
        NoticeSearchSortType finalSortType = sortType != null ? sortType : NoticeSearchSortType.LATEST;

        // 필터 설정: 마감임박순인 경우 자동으로 모집중만 표시
        NoticeSearchFilterType finalStatus;
        if (finalSortType == NoticeSearchSortType.END) {
            finalStatus = NoticeSearchFilterType.RECRUITING; // 마감임박순은 항상 모집중만
        } else {
            finalStatus = status != null ? status : NoticeSearchFilterType.ALL;
        }
        boolean filterRecruiting = (finalStatus == NoticeSearchFilterType.RECRUITING);

        // 페이징 및 정렬 설정 (page는 1부터 시작하므로 -1)
        Pageable pageable = createPageable(page - 1, size, finalSortType);

        Instant now = java.time.Instant.now();

        // MongoDB text search 실행 (Slice 방식)
        Slice<NoticeDocument> noticeSlice = noticeRepository.searchByTitleSlice(
                keyword,
                pageable,
                filterRecruiting,
                now
        );

        // 검색 결과 총 개수 조회
        long totalCount = noticeRepository.countByTitle(
                keyword,
                filterRecruiting,
                now
        );

        // 좋아요 정보 조회 (userId가 있는 경우)
        List<String> likedNoticeIds = (userId != null)
                ? likeService.getLikeNoticeIds(userId)
                : List.of();

        // DTO 변환
        List<NoticeSearchResultResponse> content = noticeSlice.getContent().stream()
                .map(notice -> {
                    boolean isLiked = likedNoticeIds.contains(notice.getId());
                    return NoticeSearchResultResponse.from(notice, isLiked);
                })
                .collect(Collectors.toList());

        // SliceResponse 생성
        return SliceResponse.<NoticeSearchResultResponse>builder()
                .totalCount(totalCount)
                .content(content)
                .hasNext(noticeSlice.hasNext())
                .page(page)
                .build();
    }

    /**
     * Pageable 객체 생성
     */
    private Pageable createPageable(int page, int size, NoticeSearchSortType sortType) {
        // 기본값 설정
        int validPage = Math.max(0, page);
        int validSize = Math.min(Math.max(1, size), 100); // 최대 100개

        // 정렬 방식 결정
        Sort sortOrder = switch (sortType) {
            case END ->
                    // 마감임박순: applyEnd 오름차순
                    Sort.by(Sort.Direction.ASC, "applyEnd");
            case LATEST ->
                    // 최신공고순: announceDate 내림차순
                    Sort.by(Sort.Direction.DESC, "announceDate");
        };

        return PageRequest.of(validPage, validSize, sortOrder);
    }
}
