package com.pinHouse.server.platform.search.application.service;

import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.platform.housing.complex.application.usecase.ComplexUseCase;
import com.pinHouse.server.platform.housing.complex.application.util.DistanceUtil;
import com.pinHouse.server.platform.housing.complex.application.dto.result.RootResult;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import com.pinHouse.server.platform.pinPoint.application.usecase.PinPointUseCase;
import com.pinHouse.server.platform.pinPoint.domain.entity.PinPoint;
import com.pinHouse.server.platform.search.application.dto.FastSearchRequest;
import com.pinHouse.server.platform.search.application.dto.FastSearchResponse;
import com.pinHouse.server.platform.search.application.usecase.FastSearchUseCase;
import com.pinHouse.server.platform.user.application.usecase.UserUseCase;
import com.pinHouse.server.platform.user.domain.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;
import java.util.function.ToIntFunction;

/**
 * 빠른 검색을 위한 로직
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FastSearchService implements FastSearchUseCase {

    /// 의존성
    private final ComplexUseCase complexService;
    private final UserUseCase userService;
    private final PinPointUseCase pinPointService;

    /// 외부 API 의존성
    private final DistanceUtil distanceUtil;

    // =================
    //  퍼블릭 로직
    // =================

    /// 검색
    @Override
    public List<FastSearchResponse> search(UUID userId, FastSearchRequest request) {

        /// 유저/핀포인트 검증
        User user = userService.loadUser(userId);

        if (!pinPointService.checkPinPoint(request.pinPointId(), user.getId())) {
            throw new IllegalArgumentException(ErrorCode.BAD_REQUEST_PINPOINT.getMessage());
        }

        /// 핀포인트 조회
        var pinPoint = pinPointService.loadPinPoint(request.pinPointId());

        /// 단지 필터링 (없으면 바로 빈 리스트)
        List<ComplexDocument> complexes = complexService.filterComplexes(request);

        if (complexes == null || complexes.isEmpty()) {
            return List.of();
        }

        /// 변환 파이프라인
        return complexes.stream()
                .map(complex -> toFastSearchResponse(complex, pinPoint))
                .toList();
    }

    /* =========================
     * 내부 로직
     * ========================= */

    /// 변환 로직
    private FastSearchResponse toFastSearchResponse(ComplexDocument complex, PinPoint pinPoint) {

        /// 위치 없으면 0
        double avgTime = 0.0;
        var loc = complex.getLocation();

        /// 거리계산 로직
        if (loc != null) {
            List<RootResult> path = findPathUnchecked(
                    loc.getLatitude(), loc.getLongitude(),
                    pinPoint.getLatitude(), pinPoint.getLongitude()
            );
            avgTime = averageInt(path, RootResult::totalTime);
        }

        /// 유닛 통계 계산(빈/널 가드 포함)
        var stats = computeUnitStats(complex.getUnitTypes());

        /// DTO 변환리턴
        return FastSearchResponse.from(
                complex,
                stats.avgAreaM2(),
                stats.avgDeposit(),
                stats.avgMonthlyRent(),
                avgTime
        );
    }

    /// 거리계산
    private List<RootResult> findPathUnchecked(double fromLat, double fromLng,
                                               double toLat, double toLng) {
        try {
            return distanceUtil.findPath(fromLat, fromLng, toLat, toLng);
        } catch (UnsupportedEncodingException e) {
            // 호출부 단순화를 위해 런타임 예외로 래핑
            throw new RuntimeException("거리 경로 조회 실패", e);
        }
    }

    /// 거리 평균
    private double averageInt(List<RootResult> list, ToIntFunction<RootResult> getter) {
        if (list == null || list.isEmpty()) return 0.0;

        return list.stream()
                .mapToInt(getter)
                .average()
                .orElse(0.0);
    }

    /// 임대주택 내부 평균
    private UnitStats computeUnitStats(List<UnitType> unitTypes) {
        if (unitTypes == null || unitTypes.isEmpty()) {
            return new UnitStats(0.0, 0L, 0);
        }

        double avgAreaM2 = unitTypes.stream()
                .mapToDouble(UnitType::getExclusiveAreaM2)
                .average()
                .orElse(0.0);

        double avgDepositD = unitTypes.stream()
                .mapToLong(u -> u.getDeposit().getTotal())
                .average()
                .orElse(0.0);

        double avgMonthlyRentD = unitTypes.stream()
                .mapToInt(UnitType::getMonthlyRent)
                .average()
                .orElse(0.0);

        long avgDeposit = Math.round(avgDepositD);
        int avgMonthlyRent = (int) Math.round(avgMonthlyRentD);

        return new UnitStats(avgAreaM2, avgDeposit, avgMonthlyRent);
    }

    /// 임대주택 내부 정보 담기
    private record UnitStats(double avgAreaM2, long avgDeposit, int avgMonthlyRent) {}

}
