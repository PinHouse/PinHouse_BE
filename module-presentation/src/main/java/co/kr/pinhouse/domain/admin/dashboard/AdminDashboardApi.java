package co.kr.pinhouse.domain.admin.dashboard;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.pinhouse.common.auth.CurrentUserId;
import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.domain.admin.dashboard.application.dto.response.AdminDashboardResponse;
import co.kr.pinhouse.domain.admin.dashboard.application.usecase.AdminDashboardUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "관리자 대시보드 API", description = "관리자 홈 대시보드 집계 API")
public class AdminDashboardApi {

	private final AdminDashboardUseCase adminDashboardService;

	@GetMapping
	@Operation(summary = "관리자 대시보드 조회", description = "대시보드 초기 렌더에 필요한 집계 데이터를 조회합니다.")
	public ApiResponse<AdminDashboardResponse> getDashboard(@CurrentUserId(required = true) UUID adminId) {
		return ApiResponse.ok(adminDashboardService.getDashboard(adminId));
	}
}
