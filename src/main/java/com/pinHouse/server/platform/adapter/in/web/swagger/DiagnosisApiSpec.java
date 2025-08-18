package com.pinHouse.server.platform.adapter.in.web.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.adapter.in.web.dto.request.DiagnosisRequestDTO;
import com.pinHouse.server.platform.adapter.in.web.dto.response.DiagnosisDTO;
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
    ApiResponse<DiagnosisDTO> diagnosis(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                        @RequestBody DiagnosisRequestDTO requestDTO);

}
