package com.pinHouse.server.platform.diagnostic.diagnosis.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisDetailResponse;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisRequest;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisResponse;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisResponseV2;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "진단 API", description = "청약 진단 기능 API 입니다")
public interface DiagnosisApiSpec {

    @Operation(
            summary = "청약 진단 API",
            description = "청약 진단을 수행하고 결과만 반환합니다."
    )
    ApiResponse<DiagnosisResponse> diagnosis(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                             @RequestBody DiagnosisRequest requestDTO);

    @Operation(
            summary = "청약 진단 API v2",
            description = "청약 진단을 수행하고 추천 결과를 공고 유형별로 그룹화한 v2 응답을 반환합니다."
    )
    ApiResponse<DiagnosisResponseV2> diagnosisV2(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                             @RequestBody DiagnosisRequest requestDTO);

    @Operation(
            summary = "최근 진단 결과 상세 조회 API",
            description = "사용자의 최근 진단 결과를 입력 정보와 함께 상세하게 조회합니다."
    )
    ApiResponse<DiagnosisDetailResponse> getLatestDiagnosis(@AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(
            summary = "최근 진단 결과 조회 API v2",
            description = "추천 임대주택을 공고 유형별로 그룹화하여 반환합니다."
    )
    ApiResponse<DiagnosisResponseV2> getLatestDiagnosisV2(@AuthenticationPrincipal PrincipalDetails principalDetails);

}
