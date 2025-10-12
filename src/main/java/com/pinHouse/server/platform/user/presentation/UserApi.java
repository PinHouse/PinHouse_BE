package com.pinHouse.server.platform.user.presentation;

import com.pinHouse.server.core.aop.CheckLogin;
import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.user.application.dto.*;
import com.pinHouse.server.platform.user.application.usecase.UserUseCase;
import com.pinHouse.server.platform.user.presentation.swagger.UserApiSpec;
import com.pinHouse.server.security.jwt.application.util.HttpUtil;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    /// 회원가입
    @PostMapping()
    public ApiResponse<Void> signUp(@RequestParam String tempKey,
                                    @RequestBody @Valid UserRequest request) {

        /// 서비스
        service.saveUser(tempKey, request);

        return ApiResponse.created();
    }

    /// 나의 정보 조회하기
    @GetMapping("/mypage")
    @CheckLogin
    public ApiResponse<MyPageResponse> getMyPage(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        /// 서비스
        var response = service.getMyPage(principalDetails.getId());

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
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        /// 서비스
        service.updateUser(request, principalDetails.getId());

        /// 리턴
        return ApiResponse.updated();
    }

    /// 회원탈퇴
    @DeleteMapping()
    @CheckLogin
    public ApiResponse<Void> delete(
            HttpServletResponse httpServletResponse,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {

        /// 서비스
        service.deleteUser(principalDetails.getId());

        /// 쿠키 삭제
        httpUtil.removeAccessTokenCookie(httpServletResponse);
        httpUtil.removeRefreshTokenCookie(httpServletResponse);

        /// 리턴
        return ApiResponse.deleted();
    }

}

