package com.pinHouse.server.platform.diagnostic.diagnosis.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisResponse;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.usecase.DiagnosisUseCase;
import com.pinHouse.server.platform.diagnostic.diagnosis.presentation.swagger.DiagnosisApiSpec;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisRequest;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse<DiagnosisResponse> diagnosis(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                   @RequestBody DiagnosisRequest request) {

        /// 서비스
        DiagnosisResponse response = service.diagnose(principalDetails.getId(), request);

        /// 리턴
        return ApiResponse.ok(response);
    }
}
