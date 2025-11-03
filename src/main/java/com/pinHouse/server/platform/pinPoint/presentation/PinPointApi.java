package com.pinHouse.server.platform.pinPoint.presentation;

import com.pinHouse.server.core.aop.CheckLogin;
import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.pinPoint.application.dto.PinPointRequest;
import com.pinHouse.server.platform.pinPoint.application.dto.PinPointResponse;
import com.pinHouse.server.platform.pinPoint.application.dto.UpdatePinPointRequest;
import com.pinHouse.server.platform.pinPoint.application.usecase.PinPointUseCase;
import com.pinHouse.server.platform.pinPoint.presentation.swagger.PinPointApiSpec;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/pinpoints")
@RequiredArgsConstructor
public class PinPointApi implements PinPointApiSpec {

    private final PinPointUseCase service;

    /// 핀포인트 생성하기
    @CheckLogin
    @PostMapping()
    public ApiResponse<Void> addPinPoint(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid PinPointRequest request) {

        /// 서비스
        service.savePinPoint(principalDetails.getId(), request);

        /// 리턴
        return ApiResponse.created();
    }

    /// 핀포인트 이름 수정하기
    @CheckLogin
    @PatchMapping("{id}")
    public ApiResponse<Void> updatePinPoint(
            @PathVariable String id,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid UpdatePinPointRequest request) {

        /// 서비스
        service.update(id, principalDetails.getId(), request);

        /// 리턴
        return ApiResponse.created();
    }


    /// 나의 핀포인트 목록 조회하기
    @CheckLogin
    @GetMapping()
    public ApiResponse<List<PinPointResponse>> getPinPoints(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {

        /// 서비스
        var responses = service.loadPinPoints(principalDetails.getId());

        /// 리턴
        return ApiResponse.ok(responses);
    }

    /// 핀포인트 제거하기
    @DeleteMapping()
    @CheckLogin
    public ApiResponse<Void> deletePinPoint(
            @RequestParam String id,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {

        /// 서비스
        service.deletePinPoint(principalDetails.getId(), id);

        /// 리턴
        return ApiResponse.deleted();
    }
}
