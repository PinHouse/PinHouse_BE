package com.pinHouse.server.platform.diagnostic.explanation.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.diagnostic.explanation.domain.entity.DiagnosisType;
import com.pinHouse.server.platform.diagnostic.explanation.application.dto.DiagnosisQuestionResponse;
import com.pinHouse.server.platform.diagnostic.explanation.application.dto.ExplanationResponse;
import com.pinHouse.server.platform.diagnostic.explanation.application.usecase.ExplanationUseCase;
import com.pinHouse.server.platform.diagnostic.explanation.presentation.swagger.ExplanationApiSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/diagnosis/info")
@RequiredArgsConstructor
public class ExplanationApi implements ExplanationApiSpec {

    private final ExplanationUseCase service;

    /**
     * 질문 받는 로직
     * @param type  기본 질문 이후, 추가 질문
     */
    @GetMapping("/question")
    public ApiResponse<List<DiagnosisQuestionResponse>> getQuestion(
            @RequestParam DiagnosisType type
    ) {

        /// 서비스
        List<DiagnosisQuestionResponse> response = service.getDiagnose(type);

        /// 리턴
        return ApiResponse.ok(response);
    }

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
