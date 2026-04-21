package co.kr.pinhouse.domain.admin.application.usecase;

import java.util.UUID;

import co.kr.pinhouse.domain.admin.application.dto.response.AdminMeResponse;
import co.kr.pinhouse.domain.user.domain.entity.User;

public interface AdminSessionUseCase {

	// =================
	//  퍼블릭 로직
	// =================

	/// 현재 관리자 세션 정보 조회
	AdminMeResponse getAdminMe(UUID userId);

	// =================
	//  외부 로직
	// =================

	/// 관리자 권한 사용자 조회
	User loadAdmin(UUID userId);
}
