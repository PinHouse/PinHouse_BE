package co.kr.pinhouse.domain.admin.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.pinhouse.common.exception.code.SecurityErrorCode;
import co.kr.pinhouse.common.response.CustomException;
import co.kr.pinhouse.domain.admin.application.dto.response.AdminMeResponse;
import co.kr.pinhouse.domain.admin.application.usecase.AdminSessionUseCase;
import co.kr.pinhouse.domain.user.domain.entity.Role;
import co.kr.pinhouse.domain.user.domain.entity.User;
import co.kr.pinhouse.domain.user.domain.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminSessionService implements AdminSessionUseCase {

	private final UserJpaRepository userRepository;

	// =================
	//  퍼블릭 로직
	// =================

	/// 현재 관리자 세션 정보 조회
	@Transactional(readOnly = true)
	@Override
	public AdminMeResponse getAdminMe(UUID userId) {
		return AdminMeResponse.from(loadAdmin(userId));
	}

	// =================
	//  외부 로직
	// =================

	/// 관리자 권한 사용자 조회
	@Transactional(readOnly = true)
	@Override
	public User loadAdmin(UUID userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(SecurityErrorCode.NOT_FOUND_ID));

		if (user.getRole() != Role.ADMIN) {
			throw new CustomException(SecurityErrorCode.FORBIDDEN);
		}

		return user;
	}
}
