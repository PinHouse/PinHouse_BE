package com.pinHouse.server.platform.diagnostic.diagnosis.application.usecase;

import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisDetailResponse;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisRequest;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisResponse;

import java.util.UUID;

public interface DiagnosisUseCase {

    /// 청약 진단하기 (결과만 반환)
    DiagnosisResponse diagnose(UUID userId, DiagnosisRequest request);

    /// 최근 진단 상세 조회하기 (입력 정보 + 결과)
    DiagnosisDetailResponse getDiagnoseDetail(UUID userId);

}
