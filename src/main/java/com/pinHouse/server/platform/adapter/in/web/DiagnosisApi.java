package com.pinHouse.server.platform.adapter.in.web;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.adapter.in.web.dto.request.DiagnosisRequestDTO;
import com.pinHouse.server.platform.adapter.in.web.dto.response.DiagnosisDTO;
import com.pinHouse.server.platform.adapter.in.web.swagger.DiagnosisApiSpec;
import com.pinHouse.server.platform.application.in.diagnosis.DiagnosisUseCase;
import com.pinHouse.server.platform.domain.diagnosis.model.DiagnosisRequest;
import com.pinHouse.server.platform.domain.diagnosis.model.DiagnosisResult;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/diagnosis")
@RequiredArgsConstructor
public class DiagnosisApi implements DiagnosisApiSpec {

    private final DiagnosisUseCase service;


    @PostMapping()
    public ApiResponse<DiagnosisDTO> diagnosis(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                               @RequestBody DiagnosisRequestDTO requestDTO) {

        /// DTO를 도메인으로 변환
        DiagnosisRequest request = requestDTO.toDomain();

        /// 서비스
        DiagnosisResult diagnose = service.diagnose(request);

        /// 리턴
        return ApiResponse.ok(DiagnosisDTO.from(diagnose));
    }

}
