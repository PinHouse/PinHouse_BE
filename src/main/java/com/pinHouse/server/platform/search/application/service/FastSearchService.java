package com.pinHouse.server.platform.search.application.service;

import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.platform.Location;
import com.pinHouse.server.platform.housing.distance.application.dto.response.DistanceResponse;
import com.pinHouse.server.platform.housing.distance.application.usecase.DistanceUseCase;
import com.pinHouse.server.platform.housing.notice.application.usecase.NoticeUseCase;
import com.pinHouse.server.platform.housing.notice.domain.entity.Notice;
import com.pinHouse.server.platform.pinPoint.application.usecase.PinPointUseCase;
import com.pinHouse.server.platform.search.application.dto.request.FastSearchRequest;
import com.pinHouse.server.platform.search.application.dto.response.FastSearchResponse;
import com.pinHouse.server.platform.search.application.usecase.FastSearchUseCase;
import com.pinHouse.server.platform.user.application.usecase.UserUseCase;
import com.pinHouse.server.platform.user.domain.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * 빠른 검색을 위한 로직
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FastSearchService implements FastSearchUseCase {

    /// 필터링 의존성
    private final NoticeUseCase noticeService;

    /// 외부 의존성
    private final UserUseCase userService;
    private final DistanceUseCase distanceService;
    private final PinPointUseCase pinPointService;

    /**
     * 빠른 검색을 하는 로직
     *
     * @param userId  유저ID
     * @param request 요청DTO
     */
    @Override
    public List<FastSearchResponse> search(UUID userId, FastSearchRequest request) {

        /// 유저가 존재하는지 체크
        User user = userService.loadUserById(userId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.USER_NOT_FOUND.getMessage()));

        /// 유저와 핀 포인트가 일치하는지 조회
        boolean checkPinPoint = pinPointService.checkPinPoint(request.getPinPointId(), user.getId());

        /// 일치하지 않는다면 예외 처리
        if (!checkPinPoint) {
            throw new IllegalArgumentException(ErrorCode.BAD_REQUEST_PINPOINT.getMessage());
        }

        /// 핀 포인트 조회
        var pinPoint = pinPointService.loadPinPoint(request.getPinPointId())
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.NOT_PINPOINT.getMessage()));

        /// 필터링 실행
        List<Notice> notices = noticeService.filterNotices(request);

        /// 나온 목록들, 거리 API 조회
        List<FastSearchResponse> responses = new ArrayList<>();

        notices.forEach(
                notice -> {
                    Location n = notice.getLocation();
                    List<DistanceResponse> servicePath;

                    try {
                        servicePath = distanceService.findPath(n.getLatitude(), n.getLongitude(), pinPoint.getLatitude(), pinPoint.getLongitude());
                        log.info("로그{}", servicePath.toString());
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }

                    double avgTime = servicePath.stream()
                            .mapToInt(DistanceResponse::totalTime)  // int 스트림으로 변환
                            .average()                              // OptionalDouble 반환
                            .orElse(0.0);                     // 값이 없을 경우 기본값

                    FastSearchResponse response = FastSearchResponse.from(notice, avgTime);
                    responses.add(response);
                }
        );

        return responses;
    }

}
