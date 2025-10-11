package com.pinHouse.server.platform.diagnostic.explanation.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "[응답][설명] 설명 응답 ", description = "설명 응답을 위한 DTO입니다.")
public record ExplanationResponse(

        @Schema(description = "설명 아이디", example = "501")
        Long explanationId,

        @Schema(description = "설명 제목", example = "진단 결과 설명")
        String title,

        @Schema(description = "설명 내용", example = "이 진단 결과는 제품 사용 패턴에 기반한 것입니다.")
        String content)
{}
