package com.pinHouse.server.platform.housingFit.explanation.application.service;

import com.pinHouse.server.platform.housingFit.explanation.application.dto.response.ExplanationResponse;
import com.pinHouse.server.platform.housingFit.explanation.application.usecase.ExplanationUseCase;
import com.pinHouse.server.platform.housingFit.explanation.domain.repository.ExplanationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ExplanationService implements ExplanationUseCase {

    private final ExplanationJpaRepository repository;

    @Override
    public ExplanationResponse getExplanation(Long questionId) {
        return null;
    }


}
