package co.kr.pinhouse.domain.admin.diagnostic;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.domain.admin.diagnostic.application.dto.response.AdminDiagnosticPolicyResponse;
import co.kr.pinhouse.domain.admin.diagnostic.application.usecase.AdminDiagnosticPolicyUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/diagnostic/policy")
@RequiredArgsConstructor
@Tag(name = "관리자 청약진단 정책 API", description = "청약진단 규칙의 기준값(나이·소득·자산) 조회 API")
public class AdminDiagnosticPolicyApi {

	private final AdminDiagnosticPolicyUseCase adminDiagnosticPolicyService;

	@GetMapping
	@Operation(
		summary = "청약진단 정책 설정값 조회",
		description = "현재 적용 중인 청약진단 기준값(나이 기준, 자산 한도, 임대유형별 소득 비율)을 조회합니다."
	)
	public ApiResponse<AdminDiagnosticPolicyResponse> getPolicy() {
		return ApiResponse.ok(adminDiagnosticPolicyService.getPolicy());
	}
}
