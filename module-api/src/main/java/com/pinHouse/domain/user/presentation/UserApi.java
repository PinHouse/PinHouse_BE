package com.pinHouse.domain.user.presentation;

import com.pinHouse.common.aop.CheckLogin;
import com.pinHouse.common.auth.CurrentUserId;
import com.pinHouse.common.response.ApiResponse;
import com.pinHouse.domain.user.application.dto.*;
import com.pinHouse.domain.user.application.usecase.UserUseCase;
import com.pinHouse.domain.user.presentation.swagger.UserApiSpec;
import com.pinHouse.common.util.HttpUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserApi implements UserApiSpec {

    private final UserUseCase service;

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
    public ApiResponse<Void> signUp(HttpServletResponse httpServletResponse ,
                                    @RequestParam String tempKey,
                                    @RequestBody @Valid UserRequest request) {

        /// 서비스 - User 엔티티 반환 (JWT 토큰은 app 모듈에서 생성)
        var user = service.saveUser(tempKey, request);

        // TODO: app 모듈에서 JWT 토큰 생성 및 쿠키 추가 로직 구현
        // JWT 토큰 생성은 security 모듈의 책임
        // 현재는 컴파일을 위해 주석 처리

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
