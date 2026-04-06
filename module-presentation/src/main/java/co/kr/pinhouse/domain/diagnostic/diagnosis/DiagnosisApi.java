package co.kr.pinhouse.domain.diagnostic.diagnosis;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.pinhouse.common.aop.CheckLogin;
import co.kr.pinhouse.common.auth.CurrentUserId;
import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.domain.diagnostic.diagnosis.application.dto.DiagnosisDetailResponse;
import co.kr.pinhouse.domain.diagnostic.diagnosis.application.dto.DiagnosisRequest;
import co.kr.pinhouse.domain.diagnostic.diagnosis.application.dto.DiagnosisResponse;
import co.kr.pinhouse.domain.diagnostic.diagnosis.application.dto.DiagnosisResponseV2;
import co.kr.pinhouse.domain.diagnostic.diagnosis.application.usecase.DiagnosisUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/v1/diagnosis", "/v2/diagnosis"})
@RequiredArgsConstructor
public class DiagnosisApi implements DiagnosisApiSpec {

	private final DiagnosisUseCase service;

	/**
	 * 청약 진단하는 로직
	 *
	 * @param request       청약 진단할 결과 내용
	 */
	@PostMapping()
	public ApiResponse<DiagnosisResponse> diagnosis(@CurrentUserId(required = true) UUID userId,
												@RequestBody DiagnosisRequest request) {

		/// 서비스
		DiagnosisResponse response = service.diagnose(userId, request);

		/// 리턴
		return ApiResponse.ok(response);
	}

	/**
	 * 청약 진단 v2 (추천 그룹화 응답)
	 */
	@PostMapping(path = "", params = "v=2")
	public ApiResponse<DiagnosisResponseV2> diagnosisV2(@CurrentUserId(required = true) UUID userId,
												@RequestBody DiagnosisRequest request) {

		/// 서비스
		DiagnosisResponseV2 response = service.diagnoseV2(userId, request);

		/// 리턴
		return ApiResponse.ok(response);
	}

	/**
	 * 최근 진단 결과 상세 조회 (입력 정보 + 결과)
	 *
	 * @return 최근 진단 상세 결과
	 */
	@GetMapping("/latest")
	@CheckLogin
	public ApiResponse<DiagnosisDetailResponse> getLatestDiagnosis(@CurrentUserId(required = true) UUID userId) {

		/// 서비스
		DiagnosisDetailResponse response = service.getDiagnoseDetail(userId);

		/// 리턴
		return ApiResponse.ok(response);
	}

	/**
	 * 최근 진단 결과 v2 (추천 그룹화)
	 */
	@GetMapping(path = "/latest", params = "v=2")
	@CheckLogin
	public ApiResponse<DiagnosisResponseV2> getLatestDiagnosisV2(@CurrentUserId(required = true) UUID userId) {
		DiagnosisResponseV2 response = service.getDiagnoseSummaryV2(userId);
		return ApiResponse.ok(response);
	}
}
