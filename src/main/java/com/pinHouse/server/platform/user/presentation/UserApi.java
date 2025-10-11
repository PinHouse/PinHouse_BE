package com.pinHouse.server.platform.user.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.user.application.dto.MyPageResponse;
import com.pinHouse.server.platform.user.application.dto.UserRequest;
import com.pinHouse.server.platform.user.application.dto.TempUserResponse;
import com.pinHouse.server.platform.user.application.dto.UserResponse;
import com.pinHouse.server.platform.user.application.usecase.UserUseCase;
import com.pinHouse.server.platform.user.presentation.swagger.UserApiSpec;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserApi implements UserApiSpec {

    private final UserUseCase service;

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

    /// 유저가 저장한, 핀포인트 저장하기
    @PatchMapping()
    public ApiResponse<Void> savePinPoint(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        /// 서비스

        return ApiResponse.created();
    }

    /// 나의 정보 조회하기
    @GetMapping("/mypage")
    public ApiResponse<MyPageResponse> getMyPage(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        /// 서비스
        MyPageResponse response = service.getMyPage(principalDetails.getId());

        /// 리턴
        return ApiResponse.ok(response);
    }

    /// 다른 유저 정보 조회하기
    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getOthetUser(
            @PathVariable UUID userId) {

        /// 서비스
        UserResponse response = service.getOtherUser(userId);

        /// 리턴
        return ApiResponse.ok(response);

    }

}

