package co.kr.pinhouse.domain.admin.cs;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.kr.pinhouse.common.auth.CurrentUserId;
import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.common.response.pageable.SliceRequest;
import co.kr.pinhouse.common.response.pageable.SliceResponse;
import co.kr.pinhouse.domain.cs.application.dto.request.AssignCsInquiryRequest;
import co.kr.pinhouse.domain.cs.application.dto.request.CreateCsInquiryMessageRequest;
import co.kr.pinhouse.domain.cs.application.dto.request.UpdateCsInquiryStatusRequest;
import co.kr.pinhouse.domain.cs.application.dto.response.CsInquiryDetailResponse;
import co.kr.pinhouse.domain.cs.application.dto.response.CsInquirySummaryResponse;
import co.kr.pinhouse.domain.cs.application.usecase.CsInquiryUseCase;
import co.kr.pinhouse.domain.cs.domain.entity.CsInquiryCategory;
import co.kr.pinhouse.domain.cs.domain.entity.CsInquiryStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/cs/inquiries")
@RequiredArgsConstructor
@Tag(name = "관리자 CS API", description = "관리자 문의 처리 API")
public class AdminCsInquiryApi {

	private final CsInquiryUseCase csInquiryService;

	/// 관리자 문의 목록 조회
	@GetMapping
	@Operation(summary = "관리자 문의 목록", description = "관리자 화면용 문의 목록을 조회합니다.")
	public ApiResponse<SliceResponse<CsInquirySummaryResponse>> getInquiries(
		@RequestParam(required = false) CsInquiryStatus status,
		@RequestParam(required = false) CsInquiryCategory category,
		SliceRequest sliceRequest
	) {
		return ApiResponse.ok(csInquiryService.getAdminInquiries(status, category, sliceRequest));
	}

	/// 관리자 문의 상세 조회
	@GetMapping("/{inquiryId}")
	@Operation(summary = "관리자 문의 상세", description = "관리자 화면용 문의 상세를 조회합니다.")
	public ApiResponse<CsInquiryDetailResponse> getInquiry(@PathVariable Long inquiryId) {
		return ApiResponse.ok(csInquiryService.getAdminInquiry(inquiryId));
	}

	/// 문의 담당자 지정
	@PatchMapping("/{inquiryId}/assign")
	@Operation(summary = "문의 담당자 지정", description = "문의 담당 관리자를 지정합니다.")
	public ApiResponse<CsInquiryDetailResponse> assignInquiry(
		@PathVariable Long inquiryId,
		@RequestBody @Valid AssignCsInquiryRequest request,
		@CurrentUserId(required = true) UUID userId,
		HttpServletRequest httpServletRequest
	) {
		return ApiResponse.ok(csInquiryService.assignInquiry(inquiryId, request.adminId(), userId, httpServletRequest));
	}

	/// 문의 상태 변경
	@PatchMapping("/{inquiryId}/status")
	@Operation(summary = "문의 상태 변경", description = "문의 처리 상태를 변경합니다.")
	public ApiResponse<CsInquiryDetailResponse> updateStatus(
		@PathVariable Long inquiryId,
		@RequestBody @Valid UpdateCsInquiryStatusRequest request,
		@CurrentUserId(required = true) UUID userId,
		HttpServletRequest httpServletRequest
	) {
		return ApiResponse.ok(csInquiryService.updateStatus(inquiryId, request.status(), userId, httpServletRequest));
	}

	/// 관리자 답변 등록
	@PostMapping("/{inquiryId}/messages")
	@Operation(summary = "관리자 답변 등록", description = "문의 스레드에 관리자 답변을 등록합니다.")
	public ApiResponse<CsInquiryDetailResponse> addMessage(
		@PathVariable Long inquiryId,
		@RequestBody @Valid CreateCsInquiryMessageRequest request,
		@CurrentUserId(required = true) UUID userId,
		HttpServletRequest httpServletRequest
	) {
		return ApiResponse.ok(csInquiryService.addAdminMessage(inquiryId, userId, request, httpServletRequest));
	}
}
