package com.pinHouse.server.platform.diagnostic.explanation.application.service;

import com.pinHouse.server.platform.diagnostic.explanation.domain.entity.DiagnosisType;
import com.pinHouse.server.platform.diagnostic.explanation.application.dto.DiagnosisQuestionResponse;
import com.pinHouse.server.platform.diagnostic.explanation.application.dto.ExplanationResponse;
import com.pinHouse.server.platform.diagnostic.explanation.application.usecase.ExplanationUseCase;
import com.pinHouse.server.platform.diagnostic.explanation.domain.repository.DiagnosisQuestionJpaRepository;
import com.pinHouse.server.platform.diagnostic.explanation.domain.repository.ExplanationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ExplanationService implements ExplanationUseCase {

    /// 질문 의존성
    private final DiagnosisQuestionJpaRepository questionRepository;

    /// 설명 의존성
    private final ExplanationJpaRepository repository;

    /**
     * 진단하기
     * @param type  타입
     */
    @Override
    public List<DiagnosisQuestionResponse> getDiagnose(DiagnosisType type) {
        return null;
    }

    @Override
    public ExplanationResponse getExplanation(Long questionId) {
        return null;
    }


}
