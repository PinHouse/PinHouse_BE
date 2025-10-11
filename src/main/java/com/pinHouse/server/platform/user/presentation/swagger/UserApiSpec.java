package com.pinHouse.server.platform.user.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.user.application.dto.TempUserResponse;
import com.pinHouse.server.platform.user.application.dto.UserRequest;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "유저 API", description = "회원가입, 정보조회에 관한 API 입니다.")
public interface UserApiSpec {

    /**
     * 회원가입 API
     *
     * @param request 회원가입 요청 데이터
     */
    @Operation(
            summary = "회원가입 API",
            description = "온보딩의 데이터를 바탕으로 회원가입합니다."
    )
    ApiResponse<Void> signUp(@RequestParam String tempKey,
                             @RequestBody @Valid UserRequest request);

    /**
     * 임시 유저 조회 API
     * @param tempKey   임시 키
     */
    @Operation(
            summary = "임시 유저 정보 조회 API",
            description = "레디스키를 바탕으로 임시 유저 정보를 조회합니다."
    )
    ApiResponse<TempUserResponse> getUser(@RequestParam String tempKey);


    /**
     * 핀포인트 배치 수정
     *
     * @param principalDetails 유저 인증정보
     */
    @Operation(
            summary = "유저 핀포인트 수정 API",
            description = "유저가 저장한, 핀포인트 저장합니다."
    )
    ApiResponse<Void> savePinPoint(@AuthenticationPrincipal PrincipalDetails principalDetails);


}
