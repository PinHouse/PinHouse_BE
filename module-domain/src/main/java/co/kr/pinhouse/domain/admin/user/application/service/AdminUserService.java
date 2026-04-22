package co.kr.pinhouse.domain.admin.user.application.service;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.pinhouse.common.exception.code.UserErrorCode;
import co.kr.pinhouse.common.response.CustomException;
import co.kr.pinhouse.common.response.pageable.SliceRequest;
import co.kr.pinhouse.common.response.pageable.SliceResponse;
import co.kr.pinhouse.domain.admin.application.usecase.AdminSessionUseCase;
import co.kr.pinhouse.domain.admin.audit.application.usecase.AdminAuditLogUseCase;
import co.kr.pinhouse.domain.admin.audit.domain.entity.AdminAuditActionType;
import co.kr.pinhouse.domain.admin.audit.domain.entity.AdminAuditTargetType;
import co.kr.pinhouse.domain.admin.user.application.dto.response.AdminUserDetailResponse;
import co.kr.pinhouse.domain.admin.user.application.dto.response.AdminUserSummaryResponse;
import co.kr.pinhouse.domain.admin.user.application.usecase.AdminUserUseCase;
import co.kr.pinhouse.domain.diagnostic.diagnosis.domain.repository.DiagnosisJpaRepository;
import co.kr.pinhouse.domain.like.domain.LikeJpaRepository;
import co.kr.pinhouse.domain.pinpoint.domain.repository.PinPointMongoRepository;
import co.kr.pinhouse.domain.user.domain.entity.Role;
import co.kr.pinhouse.domain.user.domain.entity.User;
import co.kr.pinhouse.domain.user.domain.repository.UserJpaRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminUserService implements AdminUserUseCase {

	private final UserJpaRepository userRepository;
	private final LikeJpaRepository likeRepository;
	private final PinPointMongoRepository pinPointRepository;
	private final DiagnosisJpaRepository diagnosisRepository;
	private final AdminSessionUseCase adminSessionService;
	private final AdminAuditLogUseCase adminAuditLogService;

	// =================
	//  퍼블릭 로직
	// =================

	/// 관리자 유저 목록 조회
	@Transactional(readOnly = true)
	@Override
	public SliceResponse<AdminUserSummaryResponse> getUsers(String keyword, SliceRequest sliceRequest) {
		var pageable = PageRequest.of(sliceRequest.page() - 1, sliceRequest.offSet(),
			Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));
		var page = (keyword == null || keyword.isBlank())
			? userRepository.findAll(pageable)
			: userRepository.findByNameContainingIgnoreCaseOrNicknameContainingIgnoreCaseOrEmailContainingIgnoreCase(
				keyword, keyword, keyword, pageable);

		return SliceResponse.from(page.map(user -> AdminUserSummaryResponse.of(
			user,
			maskName(user.getName()),
			maskEmail(user.getEmail()),
			maskPhone(user.getPhoneNumber())
		)), page.getTotalElements());
	}

	/// 관리자 유저 상세 조회
	@Transactional(readOnly = true)
	@Override
	public AdminUserDetailResponse getUser(UUID userId) {
		User user = loadUserWithFacilityTypes(userId);

		return toAdminUserDetailResponse(user);
	}

	/// 관리자 유저 권한 변경
	@Transactional
	@Override
	public AdminUserDetailResponse updateUserRole(
		UUID userId,
		Role role,
		UUID adminId,
		HttpServletRequest httpServletRequest
	) {
		adminSessionService.loadAdmin(adminId);

		if (adminId.equals(userId)) {
			throw new CustomException(UserErrorCode.FORBIDDEN_SELF_ROLE_CHANGE);
		}

		User user = loadUserWithFacilityTypes(userId);
		AdminUserDetailResponse before = toAdminUserDetailResponse(user);

		if (user.getRole() == role) {
			return before;
		}

		user.changeRole(role);
		AdminUserDetailResponse after = toAdminUserDetailResponse(user);

		adminAuditLogService.log(
			adminId,
			AdminAuditActionType.UPDATE,
			AdminAuditTargetType.USER,
			userId.toString(),
			"유저 권한 변경",
			before,
			after,
			httpServletRequest
		);

		return after;
	}

	// =================
	//  내부 로직
	// =================

	/// 관리자 유저 상세 응답 변환
	private AdminUserDetailResponse toAdminUserDetailResponse(User user) {
		return AdminUserDetailResponse.of(
			user,
			maskName(user.getName()),
			maskEmail(user.getEmail()),
			maskPhone(user.getPhoneNumber()),
			likeRepository.countByUser_Id(user.getId()),
			pinPointRepository.countByUserId(user.getId().toString()),
			diagnosisRepository.countByUser_Id(user.getId())
		);
	}

	/// 유저 상세 조회
	private User loadUserWithFacilityTypes(UUID userId) {
		return userRepository.findWithFacilityTypesById(userId)
			.orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND_USER));
	}

	/// 이름 마스킹
	private String maskName(String name) {
		if (name == null || name.isBlank()) {
			return null;
		}
		if (name.length() == 1) {
			return "*";
		}
		if (name.length() == 2) {
			return name.charAt(0) + "*";
		}
		return name.charAt(0) + "*" + name.charAt(name.length() - 1);
	}

	/// 이메일 마스킹
	private String maskEmail(String email) {
		if (email == null || email.isBlank() || !email.contains("@")) {
			return null;
		}
		String[] parts = email.split("@", 2);
		String localPart = parts[0];
		String domainPart = parts[1];

		if (localPart.length() <= 2) {
			return localPart.charAt(0) + "***@" + domainPart;
		}
		return localPart.substring(0, 2) + "***@" + domainPart;
	}

	/// 전화번호 마스킹
	private String maskPhone(String phoneNumber) {
		if (phoneNumber == null || phoneNumber.isBlank()) {
			return null;
		}
		if (phoneNumber.length() < 8) {
			return "****";
		}
		return phoneNumber.substring(0, 3) + "-****-" + phoneNumber.substring(phoneNumber.length() - 4);
	}
}
