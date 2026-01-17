package com.pinHouse.server.platform.home.application.service;

import com.pinHouse.server.core.exception.code.PinPointErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.core.response.response.pageable.SliceRequest;
import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.Location;
import com.pinHouse.server.platform.home.application.dto.HomeNoticeListResponse;
import com.pinHouse.server.platform.home.application.dto.HomeNoticeResponse;
import com.pinHouse.server.platform.home.application.dto.NoticeCountResponse;
import com.pinHouse.server.platform.home.application.usecase.HomeUseCase;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.complex.domain.repository.ComplexDocumentRepository;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListRequest;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import com.pinHouse.server.platform.housing.notice.domain.repository.NoticeDocumentRepository;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisDetailResponse;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.usecase.DiagnosisUseCase;
import com.pinHouse.server.platform.like.application.usecase.LikeQueryUseCase;
import com.pinHouse.server.platform.pinPoint.application.usecase.PinPointUseCase;
import com.pinHouse.server.platform.pinPoint.domain.entity.PinPoint;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchFilterType;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchResultResponse;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchSortType;
import com.pinHouse.server.platform.search.application.usecase.NoticeSearchUseCase;
import com.pinHouse.server.core.exception.code.DiagnosisErrorCode;
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
    private final ComplexDocumentRepository complexRepository;
    private final LikeQueryUseCase likeService;
    private final NoticeSearchUseCase noticeSearchService;
    private final PinPointUseCase pinPointService;
    private final DiagnosisUseCase diagnosisService;

    /**
     * 마감임박공고 조회 (PinPoint 지역 기반)
     * - PinPoint의 address에서 광역 단위와 시/군/구를 추출하여 해당 지역의 마감임박 공고를 조회
     */
    @Override
    public HomeNoticeListResponse getDeadlineApproachingNotices(
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

        // DTO 변환 (개별 공고 정보)
        List<HomeNoticeResponse> content = page.getContent().stream()
                .map(notice -> {
                    boolean isLiked = likedNoticeIds.contains(notice.getId());
                    return HomeNoticeResponse.from(notice, isLiked);
                })
                .toList();

        // 최종 응답 생성 (region + content + 페이징 정보)
        return HomeNoticeListResponse.builder()
            .region(county)
            .title(null)
            .content(content)
            .hasNext(page.hasNext())
            .totalElements(page.getTotalElements())
            .build();
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

    /**
     * 핀포인트 기준 최대 이동 시간 내 공고 개수 조회
     * - 거리 기반 근사 계산을 통해 최대 이동 시간 내 단지를 찾고
     * - 해당 단지들의 고유 공고 개수를 반환
     *
     * @param pinPointId 핀포인트 ID
     * @param maxTime 최대 이동 시간 (분)
     * @param userId 사용자 ID (PinPoint 소유권 검증용)
     * @return 공고 개수 응답
     */
    @Override
    public NoticeCountResponse getNoticeCountWithinTravelTime(String pinPointId, int maxTime, UUID userId) {
        // PinPoint 소유자 검증
        boolean isOwner = pinPointService.checkPinPoint(pinPointId, userId);
        if (!isOwner) {
            log.warn("PinPoint 소유자 불일치 - pinpointId={}, requestUserId={}", pinPointId, userId);
            throw new CustomException(PinPointErrorCode.BAD_REQUEST_PINPOINT);
        }

        // 핀포인트 조회
        PinPoint pinPoint = pinPointService.loadPinPoint(pinPointId);
        Location pinPointLocation = pinPoint.getLocation();

        // 평균 이동 속도 기반 반경 계산 (km 단위)
        // 평균 속도: 15km/h (대중교통 평균 속도)
        double avgSpeedKmh = 15.0;
        double distanceKm = (avgSpeedKmh * maxTime) / 60.0;

        // MongoDB 지오스페이셜 쿼리를 위한 라디안 단위 변환
        // Earth radius: 6378.1 km
        double radiusInRadians = distanceKm / 6378.1;

        // 반경 내 단지 조회
        List<ComplexDocument> nearbyComplexes = complexRepository.findByLocation(
                pinPointLocation.getLongitude(),
                pinPointLocation.getLatitude(),
                radiusInRadians
        );

        // 단지들의 고유한 공고 ID 추출 및 개수 계산
        long uniqueNoticeCount = nearbyComplexes.stream()
                .map(ComplexDocument::getNoticeId)
                .filter(noticeId -> noticeId != null && !noticeId.isBlank())
                .distinct()
                .count();

        log.debug("핀포인트 {} 기준 {}분 내 공고 개수: {} (반경: {}km)",
                pinPointId, maxTime, uniqueNoticeCount, distanceKm);

        return NoticeCountResponse.from(uniqueNoticeCount);
    }

    /**
     * 진단 기반 추천 공고 조회
     * - 사용자의 최근 청약 진단 결과를 기반으로 맞춤형 공고를 추천
     * - 진단 결과의 availableRentalTypes를 NoticeDocument.supplyType으로 매핑하여 필터링
     * - 마감임박순으로 정렬 (applyEnd ASC)
     * - 모든 공고 상태 포함 (모집중 + 마감)
     */
    @Override
    public HomeNoticeListResponse getRecommendedNoticesByDiagnosis(
            SliceRequest sliceRequest,
            UUID userId
    ) {
        // 1. 진단 결과 조회
        DiagnosisDetailResponse diagnosis = diagnosisService.getDiagnoseDetail(userId);

        // 2. 진단 기록 없음 처리
        if (diagnosis == null) {
            log.warn("진단 기록이 없습니다 - userId={}", userId);
            throw new CustomException(DiagnosisErrorCode.NOT_FOUND_DIAGNOSIS);
        }

        // 3. 추천 임대주택 유형 추출
        List<String> availableRentalTypes = diagnosis.availableRentalTypes();

        // 4. 자격 없는 경우 빈 응답 반환
        if (availableRentalTypes == null ||
            availableRentalTypes.isEmpty() ||
            availableRentalTypes.contains("해당 없음")) {
            log.info("추천 가능한 임대주택이 없습니다 - userId={}", userId);
            return HomeNoticeListResponse.builder()
                .region(null)
                .title("진단 기반 추천")
                .content(List.of())
                .hasNext(false)
                .totalElements(0L)
                .build();
        }

        // 5. 진단 결과 → 공고 supplyType 매핑
        List<String> targetSupplyTypes = availableRentalTypes.stream()
            .filter(type -> type != null && !type.isBlank())
            .distinct()
            .toList();

        // 진단 결과에 매핑될 공고 유형이 없는 경우 빈 응답 반환
        if (targetSupplyTypes.isEmpty()) {
            log.info("진단 결과에 매핑 가능한 주택 유형이 없습니다 - userId={}", userId);
            return HomeNoticeListResponse.builder()
                .region(null)
                .title("진단 기반 추천")
                .content(List.of())
                .hasNext(false)
                .totalElements(0L)
                .build();
        }

        log.debug("진단 기반 필터링 - rentalTypes={}, supplyTypes={}",
            availableRentalTypes, targetSupplyTypes);

        // 6. 페이징 설정 (마감임박순)
        Sort sort = Sort.by(Sort.Order.asc("applyEnd"), Sort.Order.asc("noticeId"));
        Pageable pageable = PageRequest.of(sliceRequest.page() - 1, sliceRequest.offSet(), sort);

        // 7. Repository 조회
        Page<NoticeDocument> page = noticeRepository.findRecommendedNoticesByDiagnosis(
            targetSupplyTypes,
            pageable
        );

        // 8. 좋아요 상태 조회
        List<String> likedNoticeIds = likeService.getLikeNoticeIds(userId);

        // 9. DTO 변환
        List<HomeNoticeResponse> content = page.getContent().stream()
            .map(notice -> {
                boolean isLiked = likedNoticeIds.contains(notice.getId());
                return HomeNoticeResponse.from(notice, isLiked);
            })
            .toList();

        // 10. 최종 응답
        return HomeNoticeListResponse.builder()
            .region(null)
            .title("진단 기반 추천")
            .content(content)
            .hasNext(page.hasNext())
            .totalElements(page.getTotalElements())
            .build();
    }
}
