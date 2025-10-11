package com.pinHouse.server.platform.user.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.user.application.dto.UserRequest;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "인증 API", description = "회원가입, 로그아웃, 토큰 재발급에 관한 API 입니다.")
public interface AuthApiSpec {

    /**
     * 회원가입 API
     *
     * @param request 회원가입 요청 데이터
     */
    @Operation(
            summary = "회원가입 API",
            description = "온보딩의 데이터를 바탕으로 회원가입합니다."
    )
    ApiResponse<Void> signUp(@RequestParam String tempKey, @RequestBody @Valid UserRequest request);


    /**
     * 로그아웃 API
     *
     * @param request  HTTP 요청
     * @param response HTTP 응답
     */
    @Operation(
            summary = "로그아웃 API",
            description = "리프레쉬 토큰을 쿠키 및 DB 에서 삭제합니다."
    )
    ApiResponse<Void> logout(@AuthenticationPrincipal PrincipalDetails principalDetails, HttpServletRequest request, HttpServletResponse response);


    /**
     * 토큰 재발급 API
     * @param request   HTTP 요청
     * @param response  HTTP 응답
     */
    @Operation(
            summary = "토큰 재발급 API",
            description = "액세스 토큰을 바탕으로"
    )
    ApiResponse<Void> refresh(HttpServletRequest request, HttpServletResponse response);

}
