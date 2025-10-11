package com.pinHouse.server.platform.diagnostic.explanation.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "[응답][진단 질문] 진단 질문 조회 응답", description = "진단 질문 조회를 위한 DTO입니다.")
public record DiagnosisQuestionResponse(

        @Schema(description = "질문 아이디", example = "1001")
        Long questionId,

        @Schema(description = "질문 내용", example = "이 제품을 얼마나 자주 사용하시나요?")
        String questionContent,

        @Schema(description = "질문 유형", example = "객관식")
        String questionType)
{}
