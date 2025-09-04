package com.pinHouse.server.platform.housingFit.diagnosis.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.housingFit.diagnosis.application.dto.request.DiagnosisRequest;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
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
    ApiResponse<List<RuleResult>> diagnosis(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                        @RequestBody DiagnosisRequest requestDTO);

}
