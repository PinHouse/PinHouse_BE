package com.pinHouse.domain.pinPoint.application.usecase;

import java.util.UUID;

import com.pinHouse.domain.pinPoint.application.dto.PinPointListResponse;
import com.pinHouse.domain.pinPoint.application.dto.PinPointRequest;
import com.pinHouse.domain.pinPoint.application.dto.UpdatePinPointRequest;
import com.pinHouse.domain.pinPoint.domain.entity.PinPoint;

public interface PinPointUseCase {

	// =================
	//  퍼블릭 로직
	// =================

	/// 저장
	void savePinPoint(UUID userId, PinPointRequest request);

	/// 목록 조회
	PinPointListResponse loadPinPoints(UUID userId);

	/// 수정
	void update(String id, UUID userId, UpdatePinPointRequest request);

	/// 삭제
	void deletePinPoint(UUID userId, String pinPointId);

	// =================
	//  외부 로직
	// =================

	/// 나의 핀포인트가 맞는지 체크
	boolean checkPinPoint(String pinPointId, UUID userId);

	/// 상세 조회
	PinPoint loadPinPoint(String pinPointId);


}
