package com.pinHouse.server.platform.pinPoint.application.service;

import com.pinHouse.server.core.exception.code.PinPointErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.platform.Location;
import com.pinHouse.server.platform.pinPoint.application.dto.PinPointRequest;
import com.pinHouse.server.platform.pinPoint.application.dto.PinPointResponse;
import com.pinHouse.server.platform.pinPoint.application.dto.UpdatePinPointRequest;
import com.pinHouse.server.platform.pinPoint.util.LocationUtil;
import com.pinHouse.server.platform.pinPoint.application.usecase.PinPointUseCase;
import com.pinHouse.server.platform.pinPoint.domain.entity.PinPoint;
import com.pinHouse.server.platform.pinPoint.domain.repository.PinPointJpaRepository;
import com.pinHouse.server.platform.user.application.usecase.UserUseCase;
import com.pinHouse.server.platform.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PinPointService implements PinPointUseCase {

    /// 핀포인트 JPA 의존성
    private final PinPointJpaRepository repository;

    /// 외부 의존성
    private final UserUseCase userService;
    private final LocationUtil locationTool;

    // =================
    //  퍼블릭 로직
    // =================

    /// 저장하기
    @Override
    @Transactional
    public void savePinPoint(UUID userId, PinPointRequest request) {

        /// 유저 검증
        User user = userService.loadUser(userId);

        /// 주소를 좌표로 변환
        Location location = locationTool.getLocation(request.address());

        /// 도메인 생성
        var entity = PinPoint.of(user, request.address(), request.name(), location.getLatitude(), location.getLongitude(), request.first());

        /// 저장하기
        repository.save(entity);
    }

    /// 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<PinPointResponse> loadPinPoints(UUID userId) {

        /// 유저 검증
        User user = userService.loadUser(userId);

        /// 유저가 존재하는 핀포인트 목록 조회
        List<PinPoint> pinPoints = repository.findByUser(user);

        /// Stream 돌면서 DTO 변경
        return PinPointResponse.from(pinPoints);
    }

    @Override
    @Transactional
    public void update(Long id, UUID userId, UpdatePinPointRequest request) {

        /// 영속성 컨테이너 조횐
        PinPoint pinPoint = loadPinPoint(id);

        /// 수정 (더티체킹)
        pinPoint.updateName(request.name());

    }

    /// 삭제하기
    @Override
    @Transactional
    public void deletePinPoint(UUID userId, Long pinPointId) {

        /// 조회
        Optional<PinPoint> pinPoint = loadPinPoint(pinPointId, userId);

        if (pinPoint.isPresent()) {
            /// 존재한다면 삭제
            repository.delete(pinPoint.get());
        } else {
            /// 삭제할 수 있는 권한이 없음
            throw new CustomException(PinPointErrorCode.FORBIDDEN_DELETE);
        }

    }

    // =================
    //  외부 로직
    // =================

    /// 핀포인트가 나의 저장영역이 맞는지 조회
    @Override
    public boolean checkPinPoint(Long pinPointId, UUID userId) {

        /// 유저 검증
        User user = userService.loadUser(userId);

        /// 존재여부가 중요한 것이 아니니 DB조회 1번으로 마무리하기
        return repository.existsByIdAndUser(pinPointId, user);
    }

    /// 상세 조회
    @Override
    @Transactional(readOnly = true)
    public PinPoint loadPinPoint(Long pinPointId) {

        return repository.findById(pinPointId)
                .orElseThrow(() -> new CustomException(PinPointErrorCode.NOT_FOUND_PINPOINT));
    }

    /// 유저와 함께 상세 조회
    @Transactional(readOnly = true)
    protected Optional<PinPoint> loadPinPoint(Long pinPointId, UUID userId) {

        return repository.findByIdAndUser_Id(pinPointId, userId);
    }

}
