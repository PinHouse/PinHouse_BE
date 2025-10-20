package com.pinHouse.server.platform.housing.notice.application.service;

import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.platform.housing.complex.application.usecase.ComplexUseCase;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.platform.housing.notice.application.usecase.NoticeUseCase;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import com.pinHouse.server.platform.housing.notice.domain.repository.NoticeDocumentRepository;
import com.pinHouse.server.platform.search.application.dto.FastSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService implements NoticeUseCase {

    private final NoticeDocumentRepository repository;
    private final ComplexUseCase complexService;

    // =================
    //  퍼블릭 로직
    // =================

    /// 공고 목록 조회
    @Override
    public List<NoticeListResponse> getNotices() {

        /// 전체 조회
        List<NoticeDocument> documents = repository.findAll();

        /// 리턴
        return NoticeListResponse.from(documents);
    }

    /// 공고 상세 조회
    @Override
    public NoticeDetailResponse getNotice(String noticeId) {

        /// 공고 조회
        NoticeDocument notice = loadNotice(noticeId);

        /// 조회
        List<ComplexDocument> complexes = complexService.loadComplexes(noticeId);


        /// 리턴
        return NoticeDetailResponse.from(notice, complexes);
    }

    // =================
    //  외부 로직
    // =================

    @Override
    public NoticeDocument loadNotice(String id) {
        return repository.findByNoticeId(id)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.NOT_NOTICE.getMessage()));
    }

    @Override
    public List<NoticeDocument> loadAllNotices() {
        return repository.findAll();
    }

    @Override
    public List<NoticeDocument> filterNotices(FastSearchRequest request) {
        return List.of();
    }
}
