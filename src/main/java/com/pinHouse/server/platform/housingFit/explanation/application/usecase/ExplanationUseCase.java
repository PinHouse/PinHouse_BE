package com.pinHouse.server.platform.housingFit.explanation.application.usecase;

import com.pinHouse.server.platform.housingFit.explanation.application.dto.response.ExplanationResponse;

public interface ExplanationUseCase {

    ExplanationResponse getExplanation(Long questionId);


}
