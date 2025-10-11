package com.pinHouse.server.platform.housing.notice.application.service;

import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.core.response.response.pageable.PageRequest;
import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.platform.housing.notice.application.usecase.NoticeUseCase;
import com.pinHouse.server.platform.housing.notice.domain.entity.Notice;
import com.pinHouse.server.platform.housing.notice.domain.repository.NoticeDocumentRepository;
import com.pinHouse.server.platform.search.application.dto.FastSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService implements NoticeUseCase {

    /// 의존성
    private final NoticeDocumentRepository repository;

    /// 공고 목록 조회
    @Override
    public SliceResponse<NoticeListResponse> getNotices(PageRequest sliceRequest) {

        /// Pageable 제작
        Pageable pageable = getPageable(sliceRequest);

        /// port 조회
        Page<Notice> notices = repository.findAll(pageable);

        /// DTO 변환
        List<Notice> contents = notices.getContent();
        List<NoticeListResponse> responseList = NoticeListResponse.from(contents);

        return new SliceImpl<>(responseList, pageable, contents.size());
    }

    /// 공고 상세 조횐
    @Override
    public NoticeDetailResponse getNoticeById(String noticeId) {

        /// 공고 조회
        Notice notice = getNotice(noticeId);

        /// DTO 변환
        return NoticeDetailResponse.from(notice);
    }

    /// 지역 기반 공고 목록 조회를 위한 함수 입니다.
    @Override
    public SliceResponse<NoticeListResponse> getNoticesByRegion(String region, PageRequest sliceRequest) {

        /// 페이징 변환
        Pageable pageable = getPageable(pageRequest);

        return null;
    }

    /// 공고 비교
    @Override
    public List<Notice> compareNotices(String noticeId1, String noticeId2) {
        return List.of();
    }

    /// 공통 함수 모음

    /**
     * - ID를 바탕으로 공고를 조회하는 함수입니다.
     * @param noticeId  공고 ID
     * @return          Notice 객체
     */
    private Notice getNotice(String noticeId) {
        return repository.findByNoticeId(noticeId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.NOT_NOTICE.getMessage()));
    }


    // =================
    //  외부 로직
    // =================

    @Override
    public Notice loadNotice(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.NOT_NOTICE.getMessage()));
    }

    @Override
    public List<Notice> loadAllNotices() {
        return repository.findAll();
    }

    /**
     * 필터링을 위한 함수
     * @param request   DTO
     */
    @Override
    public List<Notice> filterNotices(FastSearchRequest request) {
        return repository.findAll()
                .stream()
                .findFirst().stream()
                .toList();
    }

    /**
     * 페이지 요청을 Pageable 변환하는 함수 입니다.
     * @param pageRequest   페이지 요청 DTO
     * @return              Pageable
     */
    private Pageable getPageable(PageRequest pageRequest) {
        return org.springframework.data.domain.PageRequest.of(pageRequest.getPage() - 1, pageRequest.getSize(), Sort.by("id"));
    }

}
