package com.pinHouse.server.platform.user.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.user.application.dto.UserRequest;
import com.pinHouse.server.platform.user.application.dto.TempUserResponse;
import com.pinHouse.server.platform.user.presentation.swagger.AuthApiSpec;
import com.pinHouse.server.security.auth.application.usecase.AuthUseCase;
import com.pinHouse.server.security.jwt.application.util.HttpUtil;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthApi implements AuthApiSpec {

    private final AuthUseCase service;

    /// HTTP 서비스
    private final HttpUtil httpUtil;


    /**
     * 임시 유저 정보 조회
     * @param tempKey   조회할 키 캆
     */
    @GetMapping()
    public ApiResponse<TempUserResponse> getUser(@RequestParam String tempKey) {

        /// 서빗 실행 후, 리턴
        return ApiResponse.ok(service.getUserByKey(tempKey));
    }

    /**
     * 회원가입
     * @param request 온보딩 데이터
     */
    @PostMapping()
    public ApiResponse<Void> signUp(@RequestParam String tempKey,
                                    @RequestBody @Valid UserRequest request) {

        /// 서비스
        service.saveUser(tempKey, request);

        return ApiResponse.created();
    }

    /**
     * 유저가 저장한, 핀포인트 저장하기
     * @param principalDetails  유저
     */
    @PatchMapping()
    public ApiResponse<Void> savePinPoint(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        /// 서비스

        return ApiResponse.created();
    }


    /**
     * 로그아웃
     * @param request  HTTP 요청
     * @param response HTTP 응답
     */
    @DeleteMapping()
    public ApiResponse<Void> logout(@AuthenticationPrincipal PrincipalDetails principalDetails, HttpServletRequest request, HttpServletResponse response) {

        /// 서비스 실행
        tokenService.logout(principalDetails.getId(), request, response);

        /// 응답
        return ApiResponse.deleted();
    }

    /**
     * 리프레쉬 토큰 재발급
     * @param request   HTTP 요청
     * @param response  HTTP 응답
     */
    @PutMapping()
    public ApiResponse<Void> refresh(HttpServletRequest request, HttpServletResponse response) {

        /// 서비스 실행
        tokenService.reissueByRefreshToken(request, response);

        /// 응답
        return ApiResponse.updated();
    }


}

