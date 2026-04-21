package co.kr.pinhouse.domain.cs;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.pinhouse.common.auth.CurrentUserId;
import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.common.response.pageable.SliceRequest;
import co.kr.pinhouse.common.response.pageable.SliceResponse;
import co.kr.pinhouse.domain.cs.application.dto.request.CreateCsInquiryMessageRequest;
import co.kr.pinhouse.domain.cs.application.dto.request.CreateCsInquiryRequest;
import co.kr.pinhouse.domain.cs.application.dto.response.CsInquiryDetailResponse;
import co.kr.pinhouse.domain.cs.application.dto.response.CsInquirySummaryResponse;
import co.kr.pinhouse.domain.cs.application.usecase.CsInquiryUseCase;
import co.kr.pinhouse.security.aop.CheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/cs/inquiries")
@RequiredArgsConstructor
@Tag(name = "CS 문의 API", description = "사용자 문의 생성/조회 API")
public class CsInquiryApi {

	private final CsInquiryUseCase csInquiryService;

	/// 문의 생성
	@PostMapping
	@CheckLogin
	@Operation(summary = "문의 생성", description = "사용자 문의와 첫 메시지를 생성합니다.")
	public ApiResponse<CsInquiryDetailResponse> createInquiry(
		@RequestBody @Valid CreateCsInquiryRequest request,
		@CurrentUserId(required = true) UUID userId
	) {
		return ApiResponse.ok(csInquiryService.createInquiry(userId, request));
	}

	/// 내 문의 목록 조회
	@GetMapping
	@CheckLogin
	@Operation(summary = "내 문의 목록", description = "로그인한 사용자의 문의 목록을 조회합니다.")
	public ApiResponse<SliceResponse<CsInquirySummaryResponse>> getMyInquiries(
		SliceRequest sliceRequest,
		@CurrentUserId(required = true) UUID userId
	) {
		return ApiResponse.ok(csInquiryService.getMyInquiries(userId, sliceRequest));
	}

	/// 내 문의 상세 조회
	@GetMapping("/{inquiryId}")
	@CheckLogin
	@Operation(summary = "내 문의 상세", description = "로그인한 사용자의 문의 상세를 조회합니다.")
	public ApiResponse<CsInquiryDetailResponse> getMyInquiry(
		@PathVariable Long inquiryId,
		@CurrentUserId(required = true) UUID userId
	) {
		return ApiResponse.ok(csInquiryService.getMyInquiry(userId, inquiryId));
	}

	/// 문의 스레드에 사용자 메시지 추가
	@PostMapping("/{inquiryId}/messages")
	@CheckLogin
	@Operation(summary = "문의 추가 메시지", description = "문의 스레드에 사용자의 추가 메시지를 등록합니다.")
	public ApiResponse<CsInquiryDetailResponse> addMessage(
		@PathVariable Long inquiryId,
		@RequestBody @Valid CreateCsInquiryMessageRequest request,
		@CurrentUserId(required = true) UUID userId
	) {
		return ApiResponse.ok(csInquiryService.addUserMessage(userId, inquiryId, request));
	}
}
