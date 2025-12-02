package com.pinHouse.server.platform.diagnostic.diagnosis.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisHistoryResponse;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisRequest;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisResponse;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "진단 API", description = "청약 진단 기능 API 입니다")
public interface DiagnosisApiSpec {

    @Operation(
            summary = "청약 진단 API",
            description = "청약 진단 API 입니다."
    )
    ApiResponse<DiagnosisResponse> diagnosis(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                             @RequestBody DiagnosisRequest requestDTO);

    @Operation(
            summary = "진단 히스토리 목록 조회 API",
            description = "사용자의 모든 진단 히스토리를 최신순으로 조회합니다."
    )
    ApiResponse<List<DiagnosisHistoryResponse>> getDiagnosisHistory(@AuthenticationPrincipal PrincipalDetails principalDetails);

}
