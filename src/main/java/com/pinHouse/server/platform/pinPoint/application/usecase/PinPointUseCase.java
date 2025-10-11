package com.pinHouse.server.platform.pinPoint.application.usecase;

import com.pinHouse.server.platform.pinPoint.application.dto.PinPointRequest;
import com.pinHouse.server.platform.pinPoint.application.dto.PinPointResponse;
import com.pinHouse.server.platform.pinPoint.application.dto.UpdatePinPointRequest;
import com.pinHouse.server.platform.pinPoint.domain.entity.PinPoint;

import java.util.List;
import java.util.UUID;

public interface PinPointUseCase {

    // =================
    //  퍼블릭 로직
    // =================

    /// 저장
    void savePinPoint(UUID userId, PinPointRequest request);

    /// 목록 조회
    List<PinPointResponse> loadPinPoints(UUID userId);

    /// 수정
    void update(Long id, UUID userId, UpdatePinPointRequest request);

    /// 삭제
    void deletePinPoint(UUID userId, Long pinPointId);

    // =================
    //  외부 로직
    // =================

    /// 나의 핀포인트가 맞는지 체크
    boolean checkPinPoint(Long pinPointId, UUID userId);

    /// 상세 조회
    PinPoint loadPinPoint(Long pinPointId);


}
