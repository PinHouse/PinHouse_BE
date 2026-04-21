package co.kr.pinhouse.domain.admin.audit.application.usecase;

import java.util.UUID;

import co.kr.pinhouse.common.response.pageable.SliceRequest;
import co.kr.pinhouse.common.response.pageable.SliceResponse;
import co.kr.pinhouse.domain.admin.audit.application.dto.response.AdminAuditLogResponse;
import co.kr.pinhouse.domain.admin.audit.domain.entity.AdminAuditActionType;
import co.kr.pinhouse.domain.admin.audit.domain.entity.AdminAuditTargetType;
import jakarta.servlet.http.HttpServletRequest;

public interface AdminAuditLogUseCase {

	// =================
	//  외부 로직
	// =================

	/// 관리자 감사로그 저장
	void log(
		UUID adminId,
		AdminAuditActionType actionType,
		AdminAuditTargetType targetType,
		String targetId,
		String summary,
		Object beforeState,
		Object afterState,
		HttpServletRequest request
	);

	// =================
	//  퍼블릭 로직
	// =================

	/// 전체 감사로그 조회
	SliceResponse<AdminAuditLogResponse> getLogs(SliceRequest sliceRequest);

	/// 대상별 감사로그 조회
	SliceResponse<AdminAuditLogResponse> getLogsByTarget(
		AdminAuditTargetType targetType,
		String targetId,
		SliceRequest sliceRequest
	);
}
