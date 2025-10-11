package com.pinHouse.server.platform.diagnostic.explanation.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.diagnostic.explanation.application.dto.ExplanationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "질문 설명 API", description = "질문에 대한 추가 설명을 요청하는 API 입니다.")
public interface ExplanationApiSpec {

    @Operation(
            summary = "질문 설명 API",
            description = "질문에 대한 설명을 호출하는 함수"
    )
    public ApiResponse<ExplanationResponse> getQuestion(
            @Parameter(
                    description = "질문 ID",
                    example = "1"
            )
            @RequestParam Long questionId
    );



}
