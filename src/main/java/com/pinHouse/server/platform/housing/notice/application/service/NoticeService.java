package com.pinHouse.server.platform.housing.notice.application.service;

import com.pinHouse.server.core.exception.code.NoticeErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
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
import com.pinHouse.server.platform.like.application.usecase.LikeQueryUseCase;
import com.pinHouse.server.platform.search.application.dto.FastSearchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService implements NoticeUseCase {

    private final NoticeDocumentRepository repository;
    private final ComplexUseCase complexService;

    /// 좋아요 목록 조회
    private final LikeQueryUseCase likeService;


    // =================
    //  퍼블릭 로직
    // =================
    @Override
    @Transactional(readOnly = true)
    public SliceResponse<NoticeListResponse> getNotices(SortType.ListSortType sortType, SliceRequest req) {

        // 오늘(한국) 기준 Instant
        Instant now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant();

        // 안정적 정렬: 정렬필드 + _id
        Sort sort = (sortType == SortType.ListSortType.END)
                ? Sort.by(Sort.Order.asc("applyEnd"), Sort.Order.asc("_id"))
                : Sort.by(Sort.Order.desc("applyStart"), Sort.Order.desc("_id"));

        Pageable pageable = PageRequest.of(req.page() - 1, req.offSet(), sort);

        Page<NoticeDocument> page = (sortType == SortType.ListSortType.END)
                ? repository.findByApplyEndGreaterThanEqual(now, pageable)       // 마감 임박: 오늘 이후만
                : repository.findByAnnounceDateLessThanEqual(now, pageable);     // 최신: 오늘까지 공개된 것만

        List<NoticeListResponse> content = page.getContent().stream()
                .map(NoticeListResponse::from)
                .toList();

        return SliceResponse.from(new SliceImpl<>(content, pageable, page.hasNext()), page.getTotalElements());
    }


    /// 공고 상세 조회
    @Override
    @Transactional(readOnly = true)
    public NoticeDetailResponse getNotice(String noticeId, SortType.DetailSortType sortType) {

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

    /// 타입에 따라서 필터링 하기
    @Override
    public List<NoticeDocument> filterNotices(FastSearchRequest request) {

        // 공급 유형 집합 (한글 기준)
        Set<String> includedSubTypes = request.supplyTypes().stream()
                .flatMap(rt -> rt.getIncludedTypes().stream())
                .collect(Collectors.toSet());

        // 주택 유형 집합 (한글 기준)
        Set<String> houseTypeValues = request.houseTypes().stream()
                .map(FastSearchRequest.HouseType::getValue)  // ex: "오피스텔"
                .collect(Collectors.toSet());

        return repository.findAll().stream()
                .filter(n -> {
                    String ht = Optional.ofNullable(n.getHouseType()).orElse("").trim();
                    return houseTypeValues.contains(ht);
                })
                .filter(n -> {
                    String st = Optional.ofNullable(n.getSupplyType()).orElse("").trim();
                    return includedSubTypes.contains(st);
                })
                .toList();
    }


    // =================
    //  내부 로직
    // =================
    /// 아이디 목록에 따른 한번에 엔티티 가져오기
    protected List<NoticeDocument> loadNotices(List<String> noticeIds) {
        return repository.findByNoticeIdIn(noticeIds);
    }
}
