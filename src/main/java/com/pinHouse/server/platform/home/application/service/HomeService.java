package com.pinHouse.server.platform.home.application.service;

import com.pinHouse.server.core.exception.code.PinPointErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.core.response.response.pageable.SliceRequest;
import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.home.application.dto.HomeNoticeListResponse;
import com.pinHouse.server.platform.home.application.usecase.HomeUseCase;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListRequest;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import com.pinHouse.server.platform.housing.notice.domain.repository.NoticeDocumentRepository;
import com.pinHouse.server.platform.like.application.usecase.LikeQueryUseCase;
import com.pinHouse.server.platform.pinPoint.application.usecase.PinPointUseCase;
import com.pinHouse.server.platform.pinPoint.domain.entity.PinPoint;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchFilterType;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchResultResponse;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchSortType;
import com.pinHouse.server.platform.search.application.usecase.NoticeSearchUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 홈 화면 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService implements HomeUseCase {

    private final NoticeDocumentRepository noticeRepository;
    private final LikeQueryUseCase likeService;
    private final NoticeSearchUseCase noticeSearchService;
    private final PinPointUseCase pinPointService;

    /**
     * 마감임박공고 조회 (PinPoint 지역 기반)
     * - PinPoint의 address에서 광역 단위와 시/군/구를 추출하여 해당 지역의 마감임박 공고를 조회
     */
    @Override
    public SliceResponse<HomeNoticeListResponse> getDeadlineApproachingNotices(
            String pinpointId,
            SliceRequest sliceRequest,
            UUID userId
    ) {
        // PinPoint 소유자 검증
        boolean isOwner = pinPointService.checkPinPoint(pinpointId, userId);
        if (!isOwner) {
            log.warn("PinPoint 소유자 불일치 - pinpointId={}, requestUserId={}", pinpointId, userId);
            throw new CustomException(PinPointErrorCode.BAD_REQUEST_PINPOINT);
        }

        // PinPoint 조회
        PinPoint pinPoint = pinPointService.loadPinPoint(pinpointId);

        // PinPoint의 address에서 광역 단위(Region)와 시/군/구 추출
        NoticeListRequest.Region region = extractRegionFromAddress(pinPoint.getAddress());
        String county = extractCountyFromAddress(pinPoint.getAddress());

        // 현재 시각 (한국 시간 기준)
        Instant now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant();

        // 페이징 설정 (마감임박순 정렬)
        Sort sort = Sort.by(Sort.Order.asc("applyEnd"), Sort.Order.asc("noticeId"));
        Pageable pageable = PageRequest.of(sliceRequest.page() - 1, sliceRequest.offSet(), sort);

        // Repository에서 직접 조회
        Page<NoticeDocument> page = noticeRepository.findDeadlineApproachingNoticesByRegionAndCounty(
                region.getFullName(),
                county,
                pageable,
                now
        );

        // 좋아요 상태 조회
        List<String> likedNoticeIds = likeService.getLikeNoticeIds(userId);

        // DTO 변환 (region 정보 포함)
        List<HomeNoticeListResponse> content = page.getContent().stream()
                .map(notice -> {
                    boolean isLiked = likedNoticeIds.contains(notice.getId());
                    return HomeNoticeListResponse.from(notice, isLiked, county);
                })
                .toList();

        return SliceResponse.from(new SliceImpl<>(content, pageable, page.hasNext()), page.getTotalElements());
    }

    /**
     * 주소에서 시/군/구를 추출
     * @param address 전체 주소 (예: "경기도 성남시 분당구 정자동" 또는 "서울특별시 강남구 역삼동")
     * @return 시/군/구 이름 (예: "성남시", "강남구")
     */
    private String extractCountyFromAddress(String address) {
        if (address == null || address.isBlank()) {
            return null;
        }

        // 공백으로 주소를 분리
        String[] parts = address.trim().split("\\s+");

        // 광역 단위를 제외한 나머지 부분에서 시/군/구를 찾기
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            // "시", "군", "구"로 끝나는 첫 번째 토큰을 반환
            if (part.endsWith("시") || part.endsWith("군") || part.endsWith("구")) {
                return part;
            }
        }

        // 시/군/구를 찾지 못한 경우 null 반환 (광역시 등의 경우)
        log.debug("주소에서 시/군/구를 추출할 수 없습니다. address={}", address);
        return null;
    }

    /**
     * 주소에서 광역 단위(시/도)를 추출
     * @param address 전체 주소 (예: "서울특별시 강남구 역삼동" 또는 "서울 강남구 역삼동")
     * @return Region enum
     */
    private NoticeListRequest.Region extractRegionFromAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new CustomException(PinPointErrorCode.BAD_REQUEST_PINPOINT);
        }

        // 1차: 정확한 fullName으로 매칭 시도
        for (NoticeListRequest.Region region : NoticeListRequest.Region.values()) {
            if (address.startsWith(region.getFullName())) {
                return region;
            }
        }

        // 2차: 약칭으로 매칭 시도 (예: "서울" -> "서울특별시")
        if (address.startsWith("서울")) return NoticeListRequest.Region.SEOUL;
        if (address.startsWith("부산")) return NoticeListRequest.Region.BUSAN;
        if (address.startsWith("대구")) return NoticeListRequest.Region.DAEGU;
        if (address.startsWith("인천")) return NoticeListRequest.Region.INCHEON;
        if (address.startsWith("광주")) return NoticeListRequest.Region.GWANGJU;
        if (address.startsWith("대전")) return NoticeListRequest.Region.DAEJEON;
        if (address.startsWith("울산")) return NoticeListRequest.Region.ULSAN;
        if (address.startsWith("세종")) return NoticeListRequest.Region.SEJONG;
        if (address.startsWith("경기")) return NoticeListRequest.Region.GYEONGGI;
        if (address.startsWith("강원")) return NoticeListRequest.Region.GANGWON;
        if (address.startsWith("충북") || address.startsWith("충청북")) return NoticeListRequest.Region.CHUNGBUK;
        if (address.startsWith("충남") || address.startsWith("충청남")) return NoticeListRequest.Region.CHUNGNAM;
        if (address.startsWith("전북") || address.startsWith("전라북")) return NoticeListRequest.Region.JEONBUK;
        if (address.startsWith("전남") || address.startsWith("전라남")) return NoticeListRequest.Region.JEONNAM;
        if (address.startsWith("경북") || address.startsWith("경상북")) return NoticeListRequest.Region.GYEONGBUK;
        if (address.startsWith("경남") || address.startsWith("경상남")) return NoticeListRequest.Region.GYEONGNAM;
        if (address.startsWith("제주")) return NoticeListRequest.Region.JEJU;

        // 매칭되는 Region을 찾지 못한 경우
        log.warn("주소에서 광역 단위를 추출할 수 없습니다. address={}", address);
        throw new CustomException(PinPointErrorCode.BAD_REQUEST_PINPOINT);
    }

    /**
     * 통합 검색 (공고 제목 및 타겟 그룹 기반)
     * - NoticeSearchUseCase를 그대로 활용
     */
    @Override
    public SliceResponse<NoticeSearchResultResponse> searchNoticesIntegrated(
            String keyword,
            int page,
            int size,
            NoticeSearchSortType sortType,
            NoticeSearchFilterType status,
            UUID userId
    ) {
        return noticeSearchService.searchNotices(keyword, page, size, sortType, status, userId);
    }
}
