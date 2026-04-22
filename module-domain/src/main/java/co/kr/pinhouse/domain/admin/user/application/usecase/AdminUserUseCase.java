package co.kr.pinhouse.domain.admin.user.application.usecase;

import java.util.UUID;

import co.kr.pinhouse.common.response.pageable.SliceRequest;
import co.kr.pinhouse.common.response.pageable.SliceResponse;
import co.kr.pinhouse.domain.user.domain.entity.Role;
import co.kr.pinhouse.domain.admin.user.application.dto.response.AdminUserDetailResponse;
import co.kr.pinhouse.domain.admin.user.application.dto.response.AdminUserSummaryResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AdminUserUseCase {

	// =================
	//  퍼블릭 로직
	// =================

	/// 관리자 유저 목록 조회
	SliceResponse<AdminUserSummaryResponse> getUsers(String keyword, SliceRequest sliceRequest);

	/// 관리자 유저 상세 조회
	AdminUserDetailResponse getUser(UUID userId);

	/// 관리자 유저 권한 변경
	AdminUserDetailResponse updateUserRole(UUID userId, Role role, UUID adminId, HttpServletRequest httpServletRequest);
}
