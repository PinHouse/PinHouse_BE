package com.pinHouse.server.platform.search.application.service;

import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import com.pinHouse.server.platform.housing.notice.domain.repository.NoticeDocumentRepository;
import com.pinHouse.server.platform.like.application.usecase.LikeQueryUseCase;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchResponse;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchResultResponse;
import com.pinHouse.server.platform.search.application.usecase.NoticeSearchUseCase;
import com.pinHouse.server.platform.search.application.usecase.SearchKeywordUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
     * 공고 제목 기반 검색
     */
    @Override
    @Transactional(readOnly = true)
    public NoticeSearchResponse searchNotices(String keyword, int page, int size, String sort, String filter, UUID userId) {
        // 검색 키워드 기록 (비동기로 처리 가능)
        if (keyword != null && !keyword.trim().isEmpty()) {
            searchKeywordService.recordSearch(keyword);
        }

        // 필터 설정
        boolean filterOpen = "OPEN".equalsIgnoreCase(filter);

        // 페이징 및 정렬 설정
        Pageable pageable = createPageable(page, size, sort);

        // MongoDB text search 실행
        Page<NoticeDocument> noticePage = noticeRepository.searchByTitle(
                keyword,
                pageable,
                filterOpen,
                java.time.Instant.now()
        );

        // 좋아요 정보 조회 (userId가 있는 경우)
        List<String> likedNoticeIds = (userId != null)
                ? likeService.getLikeNoticeIds(userId)
                : List.of();

        // DTO 변환
        Page<NoticeSearchResultResponse> responsePage = noticePage.map(notice -> {
            boolean isLiked = likedNoticeIds.contains(notice.getId());
            return NoticeSearchResultResponse.from(notice, isLiked);
        });

        return NoticeSearchResponse.from(responsePage, keyword);
    }

    /**
     * Pageable 객체 생성
     */
    private Pageable createPageable(int page, int size, String sort) {
        // 기본값 설정
        int validPage = Math.max(0, page);
        int validSize = Math.min(Math.max(1, size), 100); // 최대 100개

        // 정렬 방식 결정
        Sort sortOrder;
        if ("DEADLINE".equalsIgnoreCase(sort)) {
            // 마감임박순: applyEnd 오름차순
            sortOrder = Sort.by(Sort.Direction.ASC, "applyEnd");
        } else if ("LATEST".equalsIgnoreCase(sort)) {
            // 최신순: announceDate 내림차순
            sortOrder = Sort.by(Sort.Direction.DESC, "announceDate");
        } else {
            // 기본값: 최신순
            sortOrder = Sort.by(Sort.Direction.DESC, "announceDate");
        }

        return PageRequest.of(validPage, validSize, sortOrder);
    }
}
