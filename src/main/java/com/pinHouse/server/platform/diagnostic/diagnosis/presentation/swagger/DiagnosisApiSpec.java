package com.pinHouse.server.platform.diagnostic.diagnosis.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisRequest;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisResponse;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "진단 API", description = "청약 진단 기능 API 입니다")
public interface DiagnosisApiSpec {

    @Operation(
            summary = "청약 진단 API",
            description = "청약 진단 API 입니다."
    )
    ApiResponse<DiagnosisResponse> diagnosis(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                             @RequestBody DiagnosisRequest requestDTO);

}
