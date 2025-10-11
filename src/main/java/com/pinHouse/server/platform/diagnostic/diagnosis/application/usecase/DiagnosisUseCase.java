package com.pinHouse.server.platform.diagnostic.diagnosis.application.usecase;

import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisRequest;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisResponse;

import java.util.UUID;

public interface DiagnosisUseCase {

    /// 청약 진단하기
    DiagnosisResponse diagnose(UUID userId, DiagnosisRequest request);

    /// 나의 진단 목록 조회하기
    DiagnosisResponse getDiagnose(UUID userId);

}
