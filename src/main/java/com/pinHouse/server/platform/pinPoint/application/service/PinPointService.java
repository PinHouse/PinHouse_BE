package com.pinHouse.server.platform.pinPoint.application.service;

import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.platform.Location;
import com.pinHouse.server.platform.pinPoint.application.dto.PinPointRequest;
import com.pinHouse.server.platform.pinPoint.application.dto.PinPointResponse;
import com.pinHouse.server.platform.pinPoint.util.LocationUtil;
import com.pinHouse.server.platform.pinPoint.application.usecase.PinPointUseCase;
import com.pinHouse.server.platform.pinPoint.domain.entity.PinPoint;
import com.pinHouse.server.platform.pinPoint.domain.repository.PinPointJpaRepository;
import com.pinHouse.server.platform.user.application.usecase.UserUseCase;
import com.pinHouse.server.platform.user.domain.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PinPointService implements PinPointUseCase {

    /// 핀포인트 JPA 의존성
    private final PinPointJpaRepository repository;

    /// 외부 의존성
    private final UserUseCase userUseCase;
    private final LocationUtil locationTool;

    /**
     * 저장하는 함수
     * @param userId    유저 ID
     * @param request   DTO
     */
    @Override
    public void savePinPoint(UUID userId, PinPointRequest request) {

        /// 유저 검증
        User user = getUser(userId);

        /// 주소를 좌표로 변환
        Location location = locationTool.getLocation(request.address());

        /// 도메인 생성
        var entity = PinPoint.of(user, request.address(), request.name(), location.getLatitude(), location.getLongitude(), request.first());

        /// 저장하기
        repository.save(entity);
    }

    @Override
    public List<PinPointResponse> loadPinPoints(UUID userId) {

        /// 유저 검증
        User user = getUser(userId);

        /// 유저가 존재하는 핀포인트 목록 조회
        List<PinPoint> pinPoints = repository.findByUser(user);

        /// Stream 돌면서 DTO 변경
        return PinPointResponse.from(pinPoints);
    }

    /**
     * 핀포인트가 나의 저장영역이 맞는지 조회
     * @param pinPointId    핀포인트 ID
     * @param userId        유저 ID
     */
    @Override
    public boolean checkPinPoint(Long pinPointId, UUID userId) {

        /// 유저 검증
        User user = getUser(userId);

        /// 존재여부가 중요한 것이 아니니 DB조회 1번으로 마무리하기
        return repository.existsByIdAndUser(pinPointId, user);
    }

    // 상세 조회
    @Override
    public Optional<PinPoint> loadPinPoint(Long pinPointId) {
        return repository.findById(pinPointId);
    }


    /// 공통 함수
    private User getUser(UUID userId) {
        return userUseCase.loadUserById(userId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.USER_NOT_FOUND.getMessage()));
    }
}
