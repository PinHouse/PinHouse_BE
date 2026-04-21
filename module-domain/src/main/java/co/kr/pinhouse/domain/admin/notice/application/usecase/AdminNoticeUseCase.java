package co.kr.pinhouse.domain.admin.notice.application.usecase;

import java.util.UUID;

import co.kr.pinhouse.common.response.pageable.SliceRequest;
import co.kr.pinhouse.common.response.pageable.SliceResponse;
import co.kr.pinhouse.domain.admin.notice.application.dto.request.UpdateAdminNoticeRequest;
import co.kr.pinhouse.domain.admin.notice.application.dto.response.AdminNoticeResponse;
import co.kr.pinhouse.domain.admin.notice.application.dto.response.AdminNoticeSummaryResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AdminNoticeUseCase {

	// =================
	//  퍼블릭 로직
	// =================

	/// 관리자 공고 목록 조회
	SliceResponse<AdminNoticeSummaryResponse> getNotices(String keyword, SliceRequest sliceRequest);

	/// 관리자 공고 상세 조회
	AdminNoticeResponse getNotice(String noticeId);

	/// 관리자 공고 운영 정보 수정
	AdminNoticeResponse updateNotice(
		String noticeId,
		UpdateAdminNoticeRequest request,
		UUID adminId,
		HttpServletRequest httpServletRequest
	);
}
