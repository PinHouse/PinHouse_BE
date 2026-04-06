package co.kr.pinhouse.domain.user;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.kr.pinhouse.common.aop.CheckLogin;
import co.kr.pinhouse.common.auth.CurrentUserId;
import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.common.util.HttpUtil;
import co.kr.pinhouse.domain.user.application.dto.MyPageResponse;
import co.kr.pinhouse.domain.user.application.dto.TempUserResponse;
import co.kr.pinhouse.domain.user.application.dto.UpdateFacilityTypesRequest;
import co.kr.pinhouse.domain.user.application.dto.UpdateUserRequest;
import co.kr.pinhouse.domain.user.application.dto.UserRequest;
import co.kr.pinhouse.domain.user.application.dto.UserResponse;
import co.kr.pinhouse.domain.user.application.dto.WithdrawRequest;
import co.kr.pinhouse.domain.user.application.usecase.UserUseCase;
import co.kr.pinhouse.security.auth.application.usecase.AuthUseCase;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserApi implements UserApiSpec {

	private final UserUseCase service;
	private final AuthUseCase authUseCase;

	/// 쿠키 삭제
	private final HttpUtil httpUtil;

	/// 최초 유저 정보 조횐
	@GetMapping()
	public ApiResponse<TempUserResponse> getUser(@RequestParam String tempKey) {

		/// 서빗 실행 후, 리턴
		return ApiResponse.ok(service.getUserByKey(tempKey));
	}

	/// 회원가입 (JWT 토큰 발급은 security 모듈/app 모듈에서 처리 필요)
	@PostMapping()
	public ApiResponse<Void> signUp(HttpServletResponse httpServletResponse,
		@RequestParam String tempKey,
		@RequestBody @Valid UserRequest request) {

		/// 서비스 - User 엔티티 반환
		var user = service.saveUser(tempKey, request);

		/// 회원가입 직후 즉시 로그인 상태가 되도록 토큰 발급
		var tokenResponse = authUseCase.issueTokens(user);
		httpUtil.addAccessTokenCookie(httpServletResponse, tokenResponse.accessToken());
		httpUtil.addRefreshTokenCookie(httpServletResponse, tokenResponse.refreshToken());

		/// 리턴
		return ApiResponse.created();
	}

	/// 나의 정보 조회하기
	@GetMapping("/mypage")
	@CheckLogin
	public ApiResponse<MyPageResponse> getMyPage(
		@CurrentUserId(required = true) UUID userId) {

		/// 서비스
		var response = service.getMyPage(userId);

		/// 리턴
		return ApiResponse.ok(response);
	}

	/// 다른 유저 정보 조회하기
	@GetMapping("/{userId}")
	public ApiResponse<UserResponse> getOthetUser(
		@PathVariable UUID userId) {

		/// 서비스
		var response = service.getOtherUser(userId);

		/// 리턴
		return ApiResponse.ok(response);
	}

	/// 회원정보 수정하기
	@PatchMapping("/mypage")
	@CheckLogin
	public ApiResponse<Void> updateUser(
		@RequestBody @Valid UpdateUserRequest request,
		@CurrentUserId(required = true) UUID userId) {

		/// 서비스
		service.updateUser(request, userId);

		/// 리턴
		return ApiResponse.updated();
	}

	/// 관심 시설 타입 수정하기
	@PatchMapping("/facility")
	@CheckLogin
	public ApiResponse<Void> updateFacilityTypes(
		@RequestBody @Valid UpdateFacilityTypesRequest request,
		@CurrentUserId(required = true) UUID userId) {

		/// 서비스
		service.updateFacilityTypes(request, userId);

		/// 리턴
		return ApiResponse.updated();
	}

	/// 회원탈퇴
	@DeleteMapping()
	@CheckLogin
	public ApiResponse<Void> delete(
		@RequestBody WithdrawRequest request,
		HttpServletResponse httpServletResponse,
		@CurrentUserId(required = true) UUID userId
	) {

		/// 서비스
		service.deleteUser(userId, request);

		/// 쿠키 삭제
		httpUtil.removeAccessTokenCookie(httpServletResponse);
		httpUtil.removeRefreshTokenCookie(httpServletResponse);

		/// 리턴
		return ApiResponse.deleted();
	}

}
