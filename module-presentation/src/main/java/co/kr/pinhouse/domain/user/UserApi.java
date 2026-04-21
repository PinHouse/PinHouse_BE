package co.kr.pinhouse.domain.user;

import static co.kr.pinhouse.common.util.LogSanitizer.sanitize;

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

import co.kr.pinhouse.common.auth.CurrentUserId;
import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.common.util.HttpUtil;
import co.kr.pinhouse.domain.user.application.dto.request.UpdateFacilityTypesRequest;
import co.kr.pinhouse.domain.user.application.dto.request.UpdateUserRequest;
import co.kr.pinhouse.domain.user.application.dto.request.UserRequest;
import co.kr.pinhouse.domain.user.application.dto.request.WithdrawRequest;
import co.kr.pinhouse.domain.user.application.dto.response.MyPageResponse;
import co.kr.pinhouse.domain.user.application.dto.response.SignupResponse;
import co.kr.pinhouse.domain.user.application.dto.response.TempUserResponse;
import co.kr.pinhouse.domain.user.application.dto.response.UserResponse;
import co.kr.pinhouse.domain.user.application.usecase.UserUseCase;
import co.kr.pinhouse.security.aop.CheckLogin;
import co.kr.pinhouse.security.auth.application.usecase.AuthUseCase;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	/// 회원가입 (JWT 토큰 직접 발급)
	@PostMapping()
	public ApiResponse<SignupResponse> signUp(
		@RequestParam String tempKey,
		@RequestBody @Valid UserRequest request) {

		/// 서비스 - User 엔티티 반환
		var user = service.saveUser(tempKey, request);

		/// JWT 토큰 직접 발급
		var tokenResponse = authUseCase.issueTokens(user);

		log.info("회원가입 완료 - userId: {}, 토큰 발급 완료", sanitize(user.getId()));

		/// 리턴
		return ApiResponse.ok(SignupResponse.of(
			tokenResponse.accessToken(),
			tokenResponse.refreshToken()
		));
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
