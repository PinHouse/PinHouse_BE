package com.pinHouse.server.platform.user.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.user.application.dto.MyPageResponse;
import com.pinHouse.server.platform.user.application.dto.TempUserResponse;
import com.pinHouse.server.platform.user.application.dto.UserRequest;
import com.pinHouse.server.platform.user.application.dto.UserResponse;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Tag(name = "유저 API", description = "회원가입, 정보조회에 관한 API 입니다.")
public interface UserApiSpec {

    /// 회원가입
    @Operation(
            summary = "회원가입 API",
            description = "온보딩의 데이터를 바탕으로 회원가입합니다."
    )
    ApiResponse<Void> signUp(@RequestParam String tempKey,
                             @RequestBody @Valid UserRequest request);

    /// 임시 유저 조회
    @Operation(
            summary = "임시 유저 정보 조회 API",
            description = "레디스키를 바탕으로 임시 유저 정보를 조회합니다."
    )
    ApiResponse<TempUserResponse> getUser(@RequestParam String tempKey);


    /// 핀 포인트 수정
    @Operation(
            summary = "유저 핀포인트 수정 API",
            description = "유저가 저장한, 핀포인트 저장합니다."
    )
    ApiResponse<Void> savePinPoint(@AuthenticationPrincipal PrincipalDetails principalDetails);

    /// 마이페이지
    @Operation(
            summary = "마이페이지 조회 API",
            description = "JWT를 바탕으로 나만의 정보를 조회합니다."
    )
    ApiResponse<MyPageResponse> getMyPage(
            @AuthenticationPrincipal PrincipalDetails principalDetails);

    /// 타 유저 조회
    @Operation(
            summary = "타 유저 조회 API",
            description = "유저아이디를 바탕으로 다른 유저의 정보를 조회합니다."
    )
    ApiResponse<UserResponse> getOthetUser(
            @PathVariable UUID userId);

}

