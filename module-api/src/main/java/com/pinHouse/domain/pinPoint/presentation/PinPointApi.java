package com.pinHouse.domain.pinPoint.presentation;

import java.util.UUID;

import com.pinHouse.common.aop.CheckLogin;
import com.pinHouse.common.auth.CurrentUserId;
import com.pinHouse.common.response.ApiResponse;
import com.pinHouse.domain.pinPoint.application.dto.PinPointListResponse;
import com.pinHouse.domain.pinPoint.application.dto.PinPointRequest;
import com.pinHouse.domain.pinPoint.application.dto.UpdatePinPointRequest;
import com.pinHouse.domain.pinPoint.application.usecase.PinPointUseCase;
import com.pinHouse.domain.pinPoint.presentation.swagger.PinPointApiSpec;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/pinpoints")
@RequiredArgsConstructor
public class PinPointApi implements PinPointApiSpec {

    private final PinPointUseCase service;

    /// 핀포인트 생성하기
    @CheckLogin
    @PostMapping()
    public ApiResponse<Void> addPinPoint(
            @CurrentUserId(required = true) UUID userId,
            @RequestBody @Valid PinPointRequest request) {

        /// 서비스
        service.savePinPoint(userId, request);

        /// 리턴
        return ApiResponse.created();
    }

    /// 핀포인트 이름 수정하기
    @CheckLogin
    @PatchMapping("{id}")
    public ApiResponse<Void> updatePinPoint(
            @PathVariable String id,
            @CurrentUserId(required = true) UUID userId,
            @RequestBody @Valid UpdatePinPointRequest request) {

        /// 서비스
        service.update(id, userId, request);

        /// 리턴
        return ApiResponse.created();
    }


    /// 나의 핀포인트 목록 조회하기
    @CheckLogin
    @GetMapping()
    public ApiResponse<PinPointListResponse> getPinPoints(
            @CurrentUserId(required = true) UUID userId
    ) {

        /// 서비스
        var response = service.loadPinPoints(userId);

        /// 리턴
        return ApiResponse.ok(response);
    }

    /// 핀포인트 제거하기
    @DeleteMapping()
    @CheckLogin
    public ApiResponse<Void> deletePinPoint(
            @RequestParam String id,
            @CurrentUserId(required = true) UUID userId
    ) {

        /// 서비스
        service.deletePinPoint(userId, id);

        /// 리턴
        return ApiResponse.deleted();
    }
}
