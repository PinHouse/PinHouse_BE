package com.pinHouse.server.platform.pinPoint.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.pinPoint.application.dto.request.PinPointRequest;
import com.pinHouse.server.platform.pinPoint.application.dto.response.PinPointResponse;
import com.pinHouse.server.platform.pinPoint.application.usecase.PinPointUseCase;
import com.pinHouse.server.platform.pinPoint.presentation.swagger.PinPointApiSpec;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pinpoints")
@RequiredArgsConstructor
public class PinPointApi implements PinPointApiSpec {

    private final PinPointUseCase service;

    /**
     * 핀포인트 생성하기
     *
     * @param principalDetails 사람
     * @param request          요청DTO
     */
    @PostMapping()
    public ApiResponse<Void> addPinPoint(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid PinPointRequest request) {

        /// 서비스
        service.savePinPoint(principalDetails.getId(), request);

        /// 리턴
        return ApiResponse.created();
    }


    /**
     * 나의 핀포인트 목록 조회하기
     * @param principalDetails  유저 ID
     */
    @GetMapping("/mypage")
    public ApiResponse<List<PinPointResponse>> getPinPoints(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {

        /// 서비스
        List<PinPointResponse> responses = service.loadPinPoints(principalDetails.getId());

        /// 리턴
        return ApiResponse.ok(responses);
    }
}
