package com.pinHouse.server.platform.housingFit.diagnosis.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.housingFit.diagnosis.application.usecase.DiagnosisUseCase;
import com.pinHouse.server.platform.housingFit.diagnosis.presentation.swagger.DiagnosisApiSpec;
import com.pinHouse.server.platform.housingFit.diagnosis.application.dto.request.DiagnosisRequest;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/diagnosis")
@RequiredArgsConstructor
public class DiagnosisApi implements DiagnosisApiSpec {

    private final DiagnosisUseCase service;

    /**
     * 청약 진단하는 로직
     *
     * @param principalDetails 로그인한 유저
     * @param request       청약 진단할 결과 내용
     */
    @PostMapping()
    public ApiResponse<List<RuleResult>> diagnosis(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                   @RequestBody DiagnosisRequest request) {

        /// 서비스
        List<RuleResult> diagnose = service.diagnose(request);

        /// 리턴
        return ApiResponse.ok(diagnose);
    }
}
