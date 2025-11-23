package com.pinHouse.server.platform.housing.notice.application.service;

import com.pinHouse.server.core.exception.code.NoticeErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.core.response.response.pageable.SliceRequest;
import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.housing.complex.application.usecase.ComplexUseCase;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.facility.application.dto.NoticeFacilityListResponse;
import com.pinHouse.server.platform.housing.facility.application.service.FacilityStatService;
import com.pinHouse.server.platform.housing.facility.application.usecase.FacilityUseCase;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityStatDocument;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailFilterRequest;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListRequest;
import com.pinHouse.server.platform.housing.notice.application.usecase.NoticeUseCase;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import com.pinHouse.server.platform.housing.notice.domain.repository.NoticeDocumentRepository;
import com.pinHouse.server.platform.like.application.usecase.LikeQueryUseCase;
import com.pinHouse.server.platform.search.application.dto.FastSearchRequest;
import com.pinHouse.server.platform.search.domain.entity.HouseType;
import com.pinHouse.server.platform.search.domain.entity.RentalType;
import com.pinHouse.server.platform.search.domain.entity.SearchHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
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
    private final FacilityUseCase facilityService;

    // =================
    //  퍼블릭 로직
    // =================
    @Override
    public SliceResponse<NoticeListResponse> getNotices(NoticeListRequest request, SliceRequest sliceRequest, UUID userId) {

        /// 오늘(한국) 기준 Instant
        Instant now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant();

        /// 정렬 조건 및 pageable 설정 (동적 쿼리에 포함)
        Sort sort = (request.sortType() == NoticeListRequest.ListSortType.END)
                ? Sort.by(Sort.Order.asc("applyEnd"), Sort.Order.asc("noticeId"))
                : Sort.by(Sort.Order.desc("announceDate"), Sort.Order.desc("noticeId"));
        Pageable pageable = PageRequest.of(sliceRequest.page() - 1, sliceRequest.offSet(), sort);

        /// DB 레벨 필터링을 위한 커스텀 Repository 호출
        Page<NoticeDocument> page = repository.findNoticesByFilters(request, pageable, now);

        /// 좋아요 상태 조회 (userId가 null이면 빈 목록)
        List<String> likedNoticeIds = (userId != null)
                ? likeService.getLikeNoticeIds(userId)
                : List.of();

        List<NoticeListResponse> content = page.getContent().stream()
                .map(notice -> {
                    boolean isLiked = likedNoticeIds.contains(notice.getId());
                    return NoticeListResponse.from(notice, isLiked);
                })
                .toList();

        return SliceResponse.from(new SliceImpl<>(content, pageable, page.hasNext()), page.getTotalElements());
    }

    @Override
    public Long countNotices(NoticeListRequest request) {
        return 0L;
    }

    /// 공고 상세 조회
    @Override
    @Transactional(readOnly = true)
    public NoticeDetailResponse getNotice(String noticeId, NoticeDetailFilterRequest request) {

        /// 공고 조회
        NoticeDocument notice = loadNotice(noticeId);

        /// 조회
        List<ComplexDocument> complexes = complexService.loadComplexes(noticeId);

        /// 아파트에 인프라 체크
        Map<String, NoticeFacilityListResponse> facilityMap = complexes.stream()
                .map(ComplexDocument::getId)
                .collect(Collectors.toMap(
                        id -> id,
                        facilityService::getNearFacilities
                ));

        /// 리턴
        return NoticeDetailResponse.from(notice, complexes, facilityMap);
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
        return repository.findById(id)
                .orElseThrow(() -> new CustomException(NoticeErrorCode.NOT_FOUND_NOTICE));
    }

    /// 타입에 따라서 필터링 하기
    @Override
    public List<NoticeDocument> filterNotices(SearchHistory request) {

        // 공급 유형 집합 (한글 기준)
        Set<String> includedSubTypes = request.getSupplyTypes().stream()
                .flatMap(rt -> rt.getIncludedTypes().stream())
                .collect(Collectors.toSet());

        // 타겟 유형 집합
        Set<String> rentalValues = request.getRentalTypes().stream()
                .map(RentalType::getValue)
                .collect(Collectors.toSet());

        // 주택 유형 집합 (한글 기준)
        Set<String> houseTypeValues = request.getHouseType().stream()
                .map(HouseType::getValue)
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
                .filter(n -> {
                    List<String> tgList = Optional.ofNullable(n.getTargetGroups()).orElse(List.of());
                    return tgList.stream().anyMatch(rentalValues::contains);
                })
                .toList();
    }


    // =================
    //  내부 로직
    // =================
    /// 아이디 목록에 따른 한번에 엔티티 가져오기
    protected List<NoticeDocument> loadNotices(List<String> noticeIds) {
        return repository.findByIdIn(noticeIds);
    }
}
