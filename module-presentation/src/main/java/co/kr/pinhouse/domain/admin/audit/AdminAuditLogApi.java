package co.kr.pinhouse.domain.admin.audit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.common.response.pageable.SliceRequest;
import co.kr.pinhouse.common.response.pageable.SliceResponse;
import co.kr.pinhouse.domain.admin.audit.application.dto.response.AdminAuditLogResponse;
import co.kr.pinhouse.domain.admin.audit.application.usecase.AdminAuditLogUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/audit-logs")
@RequiredArgsConstructor
@Tag(name = "관리자 감사로그 API", description = "관리자 변경 이력 조회 API")
public class AdminAuditLogApi {

	private final AdminAuditLogUseCase adminAuditLogService;

	/// 전체 감사로그 조회
	@GetMapping
	@Operation(summary = "감사로그 조회", description = "최신 관리자 감사로그를 페이지 단위로 조회합니다.")
	public ApiResponse<SliceResponse<AdminAuditLogResponse>> getLogs(SliceRequest sliceRequest) {
		return ApiResponse.ok(adminAuditLogService.getLogs(sliceRequest));
	}
}
