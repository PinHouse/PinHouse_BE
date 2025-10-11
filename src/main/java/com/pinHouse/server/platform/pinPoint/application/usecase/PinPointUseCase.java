package com.pinHouse.server.platform.pinPoint.application.usecase;

import com.pinHouse.server.platform.pinPoint.application.dto.PinPointRequest;
import com.pinHouse.server.platform.pinPoint.application.dto.PinPointResponse;
import com.pinHouse.server.platform.pinPoint.domain.entity.PinPoint;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PinPointUseCase {

    /// 저장
    void savePinPoint(UUID userId, PinPointRequest request);

    /// 목록 조회
    List<PinPointResponse> loadPinPoints(UUID userId);

    /// 외부사용
    // 나의 핀포인트가 맞는지 체크
    boolean checkPinPoint(Long pinPointId, UUID userId);

    // 상세 조회
    Optional<PinPoint> loadPinPoint(Long pinPointId);


}
