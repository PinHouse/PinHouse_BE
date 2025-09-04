package com.pinHouse.server.platform.application.in.diagnosis;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.DiagnosisQuestion;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.DiagnosisType;
import com.pinHouse.server.platform.housingFit.diagnosis.application.dto.request.DiagnosisRequest;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;

import java.util.List;

public interface DiagnosisUseCase {

    /// 청약 진단 내용 조회하기
    DiagnosisQuestion getDiagnose(DiagnosisType type);

    /// 청약 진단하기
    List<RuleResult> diagnose(DiagnosisRequest request);

}
