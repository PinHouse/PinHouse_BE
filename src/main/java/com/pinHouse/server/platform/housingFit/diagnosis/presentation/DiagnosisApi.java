package com.pinHouse.server.platform.housingFit.diagnosis.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.DiagnosisQuestion;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.DiagnosisType;
import com.pinHouse.server.platform.adapter.in.web.dto.request.DiagnosisRequestDTO;
import com.pinHouse.server.platform.housingFit.diagnosis.presentation.swagger.DiagnosisApiSpec;
import com.pinHouse.server.platform.application.in.diagnosis.DiagnosisUseCase;
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
     * @param requestDTO       청약 진단할 결과 내용
     */
    @PostMapping()
    public ApiResponse<List<RuleResult>> diagnosis(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                   @RequestBody DiagnosisRequestDTO requestDTO) {

        /// DTO를 도메인으로 변환
        DiagnosisRequest request = requestDTO.toDomain();

        /// 서비스
        List<RuleResult> diagnose = service.diagnose(request);

        /// 리턴
        return ApiResponse.ok(diagnose);
    }


    /**
     * 질문 받는 로직
     * @param type  기본 질문 이후, 추가 질문
     */
    @GetMapping("/question")
    public ApiResponse<String> getQuestion(
            @RequestParam DiagnosisType type
    ) {

        /// 설문 진단 내용 가져오기
        DiagnosisQuestion question = service.getDiagnose(type);

        /// 서비스


        /// 리턴
        return ApiResponse.ok("question");
    }
}
