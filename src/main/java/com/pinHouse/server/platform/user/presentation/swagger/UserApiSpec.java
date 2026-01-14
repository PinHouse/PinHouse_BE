package com.pinHouse.server.platform.user.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.user.application.dto.*;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
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
    ApiResponse<Void> signUp(HttpServletResponse httpServletResponse,
                             @RequestParam String tempKey,
                             @RequestBody @Valid UserRequest request);

    /// 임시 유저 조회
    @Operation(
            summary = "임시 유저 정보 조회 API",
            description = "레디스키를 바탕으로 임시 유저 정보를 조회합니다."
    )
    ApiResponse<TempUserResponse> getUser(@RequestParam String tempKey);

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
            @Parameter(description = "개발자아이디", example = "12345678-aaaa-bbbb-cccc-123456789abc")
            @PathVariable UUID userId);

    /// 수정하기
    @Operation(
            summary = "개인정보 수정 API",
            description = "JWT를 바탕으로 나만의 정보를 수정합니다."
    )
    ApiResponse<Void> updateUser(
            @RequestBody @Valid UpdateUserRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails);

    /// 탈퇴하기
    @Operation(
            summary = "탈퇴 API",
            description = "JWT를 바탕으로 탈퇴를 진행합니다. 탈퇴 사유는 0개 이상 복수 선택 가능합니다."
    )
    ApiResponse<Void> delete(
            @RequestBody WithdrawRequest request,
            HttpServletResponse httpServletResponse,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );


}

