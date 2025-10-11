package com.pinHouse.server.platform.diagnostic.explanation.application.usecase;

import com.pinHouse.server.platform.diagnostic.explanation.application.dto.DiagnosisQuestionResponse;
import com.pinHouse.server.platform.diagnostic.explanation.domain.entity.DiagnosisType;
import com.pinHouse.server.platform.diagnostic.explanation.application.dto.ExplanationResponse;
import java.util.List;

public interface ExplanationUseCase {

    /// 청약 진단 질문 조회하기
    List<DiagnosisQuestionResponse> getDiagnose(DiagnosisType type);

    /// 청약 진단 질문 설명
    ExplanationResponse getExplanation(Long questionId);


}
