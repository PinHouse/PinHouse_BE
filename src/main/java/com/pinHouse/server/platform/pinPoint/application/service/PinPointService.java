package com.pinHouse.server.platform.pinPoint.application.service;

import com.pinHouse.server.core.exception.code.PinPointErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.platform.Location;
import com.pinHouse.server.platform.pinPoint.application.dto.PinPointListResponse;
import com.pinHouse.server.platform.pinPoint.application.dto.PinPointRequest;
import com.pinHouse.server.platform.pinPoint.application.dto.UpdatePinPointRequest;
import com.pinHouse.server.platform.pinPoint.util.LocationUtil;
import com.pinHouse.server.platform.pinPoint.application.usecase.PinPointUseCase;
import com.pinHouse.server.platform.pinPoint.domain.entity.PinPoint;
import com.pinHouse.server.platform.pinPoint.domain.repository.PinPointMongoRepository;
import com.pinHouse.server.platform.user.application.usecase.UserUseCase;
import com.pinHouse.server.platform.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PinPointService implements PinPointUseCase {

    /// 핀포인트 Mongo 의존성
    private final PinPointMongoRepository repository;

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

        /// 새로운 핀포인트가 first=true인 경우, 기존 first=true인 핀포인트를 false로 변경
        if (request.first()) {
            Optional<PinPoint> existingFirstPinPoint = repository.findByUserIdAndIsFirst(user.getId().toString(), true);
            existingFirstPinPoint.ifPresent(pinPoint -> {
                pinPoint.setFirst(false);
                repository.save(pinPoint);
            });
        }

        /// 주소를 좌표로 변환
        Location location = locationTool.getLocation(request.address());

        /// 도메인 생성
        var entity = PinPoint.of(user.getId().toString(), request.address(), request.name(), location.getLatitude(), location.getLongitude(), request.first());

        /// 저장하기
        repository.save(entity);
    }

    /// 목록 조회
    @Override
    @Transactional(readOnly = true)
    public PinPointListResponse loadPinPoints(UUID userId) {

        /// 유저 검증
        User user = userService.loadUser(userId);

        /// 유저가 존재하는 핀포인트 목록을 first 기준으로 정렬하여 조회
        List<PinPoint> pinPoints = repository.findByUserIdOrderByIsFirstDesc(user.getId().toString());

        /// 유저 이름과 핀포인트 목록을 포함한 응답 반환
        return PinPointListResponse.of(user.getName(), pinPoints);
    }

    @Override
    @Transactional
    public void update(String id, UUID userId, UpdatePinPointRequest request) {

        /// 영속성 컨테이너 조회
        PinPoint pinPoint = loadPinPoint(id);

        /// isFirst가 true로 변경되는 경우, 기존 first=true인 핀포인트를 false로 변경
        if (request.first() != null && request.first() && !pinPoint.isFirst()) {
            Optional<PinPoint> existingFirstPinPoint = repository.findByUserIdAndIsFirst(userId.toString(), true);
            existingFirstPinPoint.ifPresent(existingPinPoint -> {
                existingPinPoint.setFirst(false);
                repository.save(existingPinPoint);
            });
        }

        /// 수정 (더티체킹)
        pinPoint.updateName(request.name());

        /// first 값이 null이 아닌 경우에만 업데이트
        if (request.first() != null) {
            pinPoint.setFirst(request.first());
        }

        repository.save(pinPoint);
    }

    /// 삭제하기
    @Override
    @Transactional
    public void deletePinPoint(UUID userId, String pinPointId) {

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
    public boolean checkPinPoint(String pinPointId, UUID userId) {

        /// 유저 검증
        User user = userService.loadUser(userId);

        /// 존재여부가 중요한 것이 아니니 DB조회 1번으로 마무리하기
        return repository.existsByIdAndUserId(pinPointId, user.getId().toString());
    }

    /// 상세 조회
    @Override
    @Transactional(readOnly = true)
    public PinPoint loadPinPoint(String pinPointId) {

        return repository.findById(pinPointId)
                .orElseThrow(() -> new CustomException(PinPointErrorCode.NOT_FOUND_PINPOINT));
    }

    /// 유저와 함께 상세 조회
    @Transactional(readOnly = true)
    protected Optional<PinPoint> loadPinPoint(String pinPointId, UUID userId) {

        return repository.findByIdAndUserId(pinPointId, userId.toString());
    }

}
