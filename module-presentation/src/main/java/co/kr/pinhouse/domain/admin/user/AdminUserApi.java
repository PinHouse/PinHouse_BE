package co.kr.pinhouse.domain.admin.user;

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
import co.kr.pinhouse.domain.admin.user.application.dto.request.UpdateAdminUserRoleRequest;
import co.kr.pinhouse.domain.admin.user.application.dto.response.AdminUserDetailResponse;
import co.kr.pinhouse.domain.admin.user.application.dto.response.AdminUserSummaryResponse;
import co.kr.pinhouse.domain.admin.user.application.usecase.AdminUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "관리자 유저 API", description = "관리자 유저 조회 API")
public class AdminUserApi {

	private final AdminUserUseCase adminUserService;

	/// 관리자 유저 목록 조회
	@GetMapping
	@Operation(summary = "관리자 유저 목록 조회", description = "마스킹된 유저 목록을 조회합니다.")
	public ApiResponse<SliceResponse<AdminUserSummaryResponse>> getUsers(
		@RequestParam(required = false) String keyword,
		SliceRequest sliceRequest
	) {
		return ApiResponse.ok(adminUserService.getUsers(keyword, sliceRequest));
	}

	/// 관리자 유저 상세 조회
	@GetMapping("/{userId}")
	@Operation(summary = "관리자 유저 상세 조회", description = "마스킹된 유저 상세와 활동 요약을 조회합니다.")
	public ApiResponse<AdminUserDetailResponse> getUser(@PathVariable UUID userId) {
		return ApiResponse.ok(adminUserService.getUser(userId));
	}

	/// 관리자 유저 권한 변경
	@PatchMapping("/{userId}/role")
	@Operation(summary = "관리자 유저 권한 변경", description = "유저의 권한을 변경합니다.")
	public ApiResponse<AdminUserDetailResponse> updateUserRole(
		@PathVariable UUID userId,
		@RequestBody @Valid UpdateAdminUserRoleRequest request,
		@CurrentUserId(required = true) UUID adminId,
		HttpServletRequest httpServletRequest
	) {
		return ApiResponse.ok(adminUserService.updateUserRole(userId, request.role(), adminId, httpServletRequest));
	}
}
