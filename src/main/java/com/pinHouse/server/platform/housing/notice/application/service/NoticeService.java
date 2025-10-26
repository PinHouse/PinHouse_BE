package com.pinHouse.server.platform.housing.notice.application.service;

import com.pinHouse.server.core.exception.code.NoticeErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.platform.housing.complex.application.usecase.ComplexUseCase;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.platform.housing.notice.application.usecase.NoticeUseCase;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import com.pinHouse.server.platform.housing.notice.domain.repository.NoticeDocumentRepository;
import com.pinHouse.server.platform.like.application.usecase.LikeQueryUseCase;
import com.pinHouse.server.platform.search.application.dto.FastSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoticeService implements NoticeUseCase {

    private final NoticeDocumentRepository repository;
    private final ComplexUseCase complexService;

    /// 좋아요 목록 조회
    private final LikeQueryUseCase likeService;


    // =================
    //  퍼블릭 로직
    // =================

    /// 공고 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<NoticeListResponse> getNotices() {

        /// 전체 조회
        List<NoticeDocument> documents = repository.findAll();

        /// 리턴
        return NoticeListResponse.from(documents);
    }

    /// 공고 상세 조회
    @Override
    @Transactional(readOnly = true)
    public NoticeDetailResponse getNotice(String noticeId) {

        /// 공고 조회
        NoticeDocument notice = loadNotice(noticeId);

        /// 조회
        List<ComplexDocument> complexes = complexService.loadComplexes(noticeId);


        /// 리턴
        return NoticeDetailResponse.from(notice, complexes);
    }

    /// 좋아요 누른 공고 목록
    @Override
    @Transactional(readOnly = true)
    public List<NoticeListResponse> getNoticesLike(UUID userId) {

        /// 방 ID 목록 조회
        List<String> noticeIds = likeService.getLikeNoticeIds(userId);

        /// 한번에 조회
        List<NoticeDocument> notices = loadNotices(noticeIds);

        /// DTO 변환
        return notices.stream()
                .map(n -> NoticeListResponse.from(n, true))
                .toList();
    }

    // =================
    //  외부 로직
    // =================

    @Override
    @Transactional
    public NoticeDocument loadNotice(String id) {
        return repository.findByNoticeId(id)
                .orElseThrow(() -> new CustomException(NoticeErrorCode.NOT_FOUND_NOTICE));
    }

    @Override
    @Transactional
    public List<NoticeDocument> loadAllNotices() {
        return repository.findAll();
    }

    @Override
    public List<NoticeDocument> filterNotices(FastSearchRequest request) {
        return List.of();
    }

    // =================
    //  내부 로직
    // =================
    /// 아이디 목록에 따른 한번에 엔티티 가져오기
    protected List<NoticeDocument> loadNotices(List<String> noticeIds) {
        return repository.findByNoticeIdIn(noticeIds);
    }
}
