package co.kr.pinhouse.domain.admin.notice;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.kr.pinhouse.common.auth.CurrentUserId;
import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.common.response.pageable.SliceRequest;
import co.kr.pinhouse.common.response.pageable.SliceResponse;
import co.kr.pinhouse.domain.admin.audit.application.dto.response.AdminAuditLogResponse;
import co.kr.pinhouse.domain.admin.audit.application.usecase.AdminAuditLogUseCase;
import co.kr.pinhouse.domain.admin.audit.domain.entity.AdminAuditTargetType;
import co.kr.pinhouse.domain.admin.notice.application.dto.request.UpdateAdminNoticeRequest;
import co.kr.pinhouse.domain.admin.notice.application.dto.response.AdminNoticeResponse;
import co.kr.pinhouse.domain.admin.notice.application.dto.response.AdminNoticeSummaryResponse;
import co.kr.pinhouse.domain.admin.notice.application.usecase.AdminNoticeUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/notices")
@RequiredArgsConstructor
@Tag(name = "관리자 공고 API", description = "관리자 공고 조회/수정 API")
public class AdminNoticeApi {

	private final AdminNoticeUseCase adminNoticeService;
	private final AdminAuditLogUseCase adminAuditLogService;

	/// 관리자 공고 목록 조회
	@GetMapping
	@Operation(summary = "관리자 공고 목록 조회", description = "운영 공고 목록을 조회합니다.")
	public ApiResponse<SliceResponse<AdminNoticeSummaryResponse>> getNotices(
		@RequestParam(required = false) String keyword,
		SliceRequest sliceRequest
	) {
		return ApiResponse.ok(adminNoticeService.getNotices(keyword, sliceRequest));
	}

	/// 관리자 공고 상세 조회
	@GetMapping("/{noticeId}")
	@Operation(summary = "관리자 공고 상세 조회", description = "운영 공고 상세와 override 정보를 조회합니다.")
	public ApiResponse<AdminNoticeResponse> getNotice(@PathVariable String noticeId) {
		return ApiResponse.ok(adminNoticeService.getNotice(noticeId));
	}

	/// 관리자 공고 운영 정보 수정
	@PatchMapping("/{noticeId}")
	@Operation(summary = "관리자 공고 수정", description = "운영용 공고 override 정보를 수정합니다.")
	public ApiResponse<AdminNoticeResponse> updateNotice(
		@PathVariable String noticeId,
		@RequestBody UpdateAdminNoticeRequest request,
		@CurrentUserId(required = true) UUID userId,
		HttpServletRequest httpServletRequest
	) {
		return ApiResponse.ok(adminNoticeService.updateNotice(noticeId, request, userId, httpServletRequest));
	}

	/// 공고 수정 이력 조회
	@GetMapping("/{noticeId}/history")
	@Operation(summary = "공고 수정 이력 조회", description = "특정 공고의 감사로그 이력을 조회합니다.")
	public ApiResponse<SliceResponse<AdminAuditLogResponse>> getHistory(
		@PathVariable String noticeId,
		SliceRequest sliceRequest
	) {
		return ApiResponse.ok(adminAuditLogService.getLogsByTarget(AdminAuditTargetType.NOTICE, noticeId, sliceRequest));
	}
}
