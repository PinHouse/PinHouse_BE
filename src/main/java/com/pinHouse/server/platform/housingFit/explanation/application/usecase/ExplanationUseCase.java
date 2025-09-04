package com.pinHouse.server.platform.housingFit.explanation.application.usecase;

import com.pinHouse.server.platform.housingFit.explanation.application.dto.response.DiagnosisQuestionResponse;
import com.pinHouse.server.platform.housingFit.explanation.domain.entity.DiagnosisType;
import com.pinHouse.server.platform.housingFit.explanation.application.dto.response.ExplanationResponse;
import java.util.List;

public interface ExplanationUseCase {

    /// 청약 진단 질문 조회하기
    List<DiagnosisQuestionResponse> getDiagnose(DiagnosisType type);

    /// 청약 진단 질문 설명
    ExplanationResponse getExplanation(Long questionId);


}
