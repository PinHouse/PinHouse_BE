package co.kr.pinhouse.domain.cs.application.usecase;

import java.util.UUID;

import co.kr.pinhouse.common.response.pageable.SliceRequest;
import co.kr.pinhouse.common.response.pageable.SliceResponse;
import co.kr.pinhouse.domain.cs.application.dto.request.CreateCsInquiryMessageRequest;
import co.kr.pinhouse.domain.cs.application.dto.request.CreateCsInquiryRequest;
import co.kr.pinhouse.domain.cs.application.dto.response.CsInquiryDetailResponse;
import co.kr.pinhouse.domain.cs.application.dto.response.CsInquirySummaryResponse;
import co.kr.pinhouse.domain.cs.domain.entity.CsInquiryCategory;
import co.kr.pinhouse.domain.cs.domain.entity.CsInquiryStatus;
import jakarta.servlet.http.HttpServletRequest;

public interface CsInquiryUseCase {

	// =================
	//  사용자 로직
	// =================

	/// 사용자 문의 생성
	CsInquiryDetailResponse createInquiry(UUID userId, CreateCsInquiryRequest request);

	/// 내 문의 목록 조회
	SliceResponse<CsInquirySummaryResponse> getMyInquiries(UUID userId, SliceRequest sliceRequest);

	/// 내 문의 상세 조회
	CsInquiryDetailResponse getMyInquiry(UUID userId, Long inquiryId);

	/// 사용자 추가 메시지 등록
	CsInquiryDetailResponse addUserMessage(
		UUID userId,
		Long inquiryId,
		CreateCsInquiryMessageRequest request
	);

	// =================
	//  관리자 로직
	// =================

	/// 관리자 문의 목록 조회
	SliceResponse<CsInquirySummaryResponse> getAdminInquiries(
		CsInquiryStatus status,
		CsInquiryCategory category,
		SliceRequest sliceRequest
	);

	/// 관리자 문의 상세 조회
	CsInquiryDetailResponse getAdminInquiry(Long inquiryId);

	/// 문의 담당 관리자 지정
	CsInquiryDetailResponse assignInquiry(
		Long inquiryId,
		UUID assigneeAdminId,
		UUID actorAdminId,
		HttpServletRequest httpServletRequest
	);

	/// 문의 상태 변경
	CsInquiryDetailResponse updateStatus(
		Long inquiryId,
		CsInquiryStatus status,
		UUID adminId,
		HttpServletRequest httpServletRequest
	);

	/// 관리자 답변 등록
	CsInquiryDetailResponse addAdminMessage(
		Long inquiryId,
		UUID adminId,
		CreateCsInquiryMessageRequest request,
		HttpServletRequest httpServletRequest
	);
}
