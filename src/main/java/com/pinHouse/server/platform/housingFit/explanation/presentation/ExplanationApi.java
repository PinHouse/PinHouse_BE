package com.pinHouse.server.platform.housingFit.explanation.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.DiagnosisQuestion;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.DiagnosisType;
import com.pinHouse.server.platform.housingFit.explanation.application.dto.response.ExplanationResponse;
import com.pinHouse.server.platform.housingFit.explanation.application.usecase.ExplanationUseCase;
import com.pinHouse.server.platform.housingFit.explanation.presentation.swagger.ExplanationApiSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/diagnosis/info")
@RequiredArgsConstructor
public class ExplanationApi implements ExplanationApiSpec {

    private final ExplanationUseCase service;

    /**
     * 질문에 보조 설명을 받는 로직
     *
     * @param questionId    궁금한 질문의 Id
     */
    @GetMapping()
    public ApiResponse<ExplanationResponse> getQuestion(
            @RequestParam Long questionId
    ) {
        /// 서비스
        ExplanationResponse explanation = service.getExplanation(questionId);

        /// 리턴
        return ApiResponse.ok(explanation);
    }
}
