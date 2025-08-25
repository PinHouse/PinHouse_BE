package com.pinHouse.server.platform.application.in.diagnosis;

import com.pinHouse.server.platform.domain.diagnosis.entity.DiagnosisQuestion;
import com.pinHouse.server.platform.domain.diagnosis.entity.DiagnosisType;
import com.pinHouse.server.platform.domain.diagnosis.model.DiagnosisRequest;
import com.pinHouse.server.platform.domain.diagnosis.model.DiagnosisResult;

public interface DiagnosisUseCase {

    /// 청약 진단 내용 조회하기
    DiagnosisQuestion getDiagnose(DiagnosisType type);

    /// 청약 진단하기
    DiagnosisResult diagnose(DiagnosisRequest request);

}
