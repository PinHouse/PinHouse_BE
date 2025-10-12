package com.pinHouse.server.platform.housing.notice.application.service;

import com.pinHouse.server.core.response.response.pageable.SliceRequest;
import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.housing.complex.application.usecase.ComplexUseCase;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.SortType;
import com.pinHouse.server.platform.housing.notice.application.usecase.NoticeUseCase;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import com.pinHouse.server.platform.housing.notice.domain.repository.NoticeDocumentRepository;
import com.pinHouse.server.platform.search.application.dto.FastSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    public SliceResponse<NoticeListResponse> getNotices(SortType sortType, SliceRequest req) {

        // 오늘(한국) 기준 Instant
        Instant now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant();

        // 안정적 정렬: 정렬필드 + _id
        Sort sort = (sortType == SortType.END)
                ? Sort.by(Sort.Order.asc("applyEnd"), Sort.Order.asc("_id"))
                : Sort.by(Sort.Order.desc("applyStart"), Sort.Order.desc("_id"));

        Pageable pageable = PageRequest.of(req.page() - 1, req.offSet(), sort);

        Page<NoticeDocument> page = (sortType == SortType.END)
                ? repository.findByApplyEndGreaterThanEqual(now, pageable)       // 마감 임박: 오늘 이후만
                : repository.findByAnnounceDateLessThanEqual(now, pageable);     // 최신: 오늘까지 공개된 것만

        List<NoticeListResponse> content = page.getContent().stream()
                .map(NoticeListResponse::from)
                .toList();

        return SliceResponse.from(new SliceImpl<>(content, pageable, page.hasNext()));
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
                .orElseThrow(NoSuchElementException::new);
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
