package co.kr.pinhouse.domain.admin;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.pinhouse.common.auth.CurrentUserId;
import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.domain.admin.application.dto.response.AdminMeResponse;
import co.kr.pinhouse.domain.admin.application.usecase.AdminSessionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
@Tag(name = "관리자 세션 API", description = "관리자 세션 확인 API")
public class AdminSessionApi {

	private final AdminSessionUseCase adminSessionService;

	/// 현재 관리자 세션 조회
	@GetMapping("/me")
	@Operation(summary = "관리자 세션 조회", description = "현재 로그인한 사용자의 관리자 정보를 조회합니다.")
	public ApiResponse<AdminMeResponse> getAdminMe(@CurrentUserId(required = true) UUID userId) {
		return ApiResponse.ok(adminSessionService.getAdminMe(userId));
	}
}
