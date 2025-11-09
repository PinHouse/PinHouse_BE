package com.pinHouse.server.platform.search.application.service;

import com.pinHouse.server.core.exception.code.PinPointErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.platform.housing.complex.application.usecase.ComplexUseCase;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.facility.application.usecase.FacilityUseCase;
import com.pinHouse.server.platform.housing.notice.application.usecase.NoticeUseCase;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import com.pinHouse.server.platform.pinPoint.application.usecase.PinPointUseCase;
import com.pinHouse.server.platform.search.application.dto.*;
import com.pinHouse.server.platform.search.application.usecase.FastSearchUseCase;
import com.pinHouse.server.platform.search.domain.entity.SearchHistory;
import com.pinHouse.server.platform.search.domain.repository.SearchHistoryMongoRepository;
import com.pinHouse.server.platform.user.application.usecase.UserUseCase;
import com.pinHouse.server.platform.user.domain.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 빠른 검색을 위한 로직
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FastSearchService implements FastSearchUseCase {

    private final SearchHistoryMongoRepository repository;

    /// 의존성
    private final ComplexUseCase complexService;
    private final UserUseCase userService;
    private final PinPointUseCase pinPointService;
    private final FacilityUseCase facilityService;
    private final NoticeUseCase noticeService;

    // =================
    //  퍼블릭 로직
    // =================

    @Override
    public SearchHistoryResponse searchHistory(UUID userId) {

        /// 유저/핀포인트 검증
        User user = userService.loadUser(userId);

        /// DB에서 리스트로 가져오기
        List<SearchHistory> histories = repository.findByUserId(user.getId().toString());

        /// 없으면 빈 응답
        if (histories == null || histories.isEmpty()) {
            return SearchHistoryResponse.of();
        }

        /// 있으면 첫 번째 사용
        SearchHistory first = histories.getFirst();

        return SearchHistoryResponse.of(first);
    }

    /// 검색
    @Override
    public FastSearchResponse search(UUID userId, FastSearchRequest request) {

        /// 유저/핀포인트 검증
        User user = userService.loadUser(userId);

        /// 핀포인트 조회 및 예외처리
        var pinPoint = pinPointService.loadPinPoint(request.pinPointId());
        if (!pinPointService.checkPinPoint(pinPoint.getId(), user.getId())) {
            throw new CustomException(PinPointErrorCode.BAD_REQUEST_PINPOINT);
        }

        /// 기록 저장하기
        var reqHistory = SearchHistory.of(String.valueOf(userId), request.pinPointId(), request.transitTime(), request.minSize(), request.maxSize(), request.maxDeposit(), request.maxMonthPay(), request.facilities(), request.rentalTypes(), request.supplyTypes(), request.houseTypes());
        repository.save(reqHistory);

        /// 공고 타입 & 주택 유형 분류하기
        List<NoticeDocument> notices = noticeService.filterNotices(request);

        /// 해당하는 인프라가 존재하는 친구로 조회
        List<ComplexDocument> facilityDocuments = facilityService.filterComplexesByFacility(notices, request.facilities());

        /// 거리 필터링
        List<ComplexDistanceResponse> documents = complexService.filterDistanceOnly(facilityDocuments, request);

        /// 전용면적/보증금/월임대료 필터링
        List<ComplexDistanceResponse> filtered = complexService.filterUnitTypesOnly(documents, request);

        /// 없다면 빈 리스트 제공
        if (filtered.isEmpty()) {
            FastSearchResponse.from(List.of());
        }

        /// DTO 변환
        List<FastUnitTypeResponse> responses = filtered.stream()
                .map(c -> FastUnitTypeResponse.from(c, facilityService.getFacilities(c.complex().getId())))
                .toList();

        /// DTO 변환 리턴
        return FastSearchResponse.from(responses);
    }

    // =================
    //  내부 로직
    // =================

}
