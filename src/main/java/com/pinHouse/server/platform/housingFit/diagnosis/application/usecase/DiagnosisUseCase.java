package com.pinHouse.server.platform.housingFit.diagnosis.application.usecase;

import com.pinHouse.server.platform.housingFit.diagnosis.application.dto.request.DiagnosisRequest;
import com.pinHouse.server.platform.housingFit.diagnosis.application.dto.response.DiagnosisResponse;

import java.util.UUID;

public interface DiagnosisUseCase {

    /// 청약 진단하기
    DiagnosisResponse diagnose(UUID userId, DiagnosisRequest request);

    /// 나의 진단 목록 조회하기
    DiagnosisResponse getDiagnose(UUID userId);

}
