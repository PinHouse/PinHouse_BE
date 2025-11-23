package com.pinHouse.server.platform.housing.complex.presentation;

import com.pinHouse.server.core.aop.CheckLogin;
import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.response.DistanceResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.response.UnitTypeResponse;
import com.pinHouse.server.platform.housing.complex.application.usecase.ComplexUseCase;
import com.pinHouse.server.platform.housing.complex.application.dto.response.ComplexDetailResponse;
import com.pinHouse.server.platform.housing.complex.presentation.swagger.ComplexApiSpec;
import com.pinHouse.server.platform.like.application.dto.UnityTypeLikeResponse;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 예산 시뮬레이터 관련 API 입니다
 */
@RestController
@RequestMapping("/v1/complexes")
@RequiredArgsConstructor
public class ComplexApi implements ComplexApiSpec {

    private final ComplexUseCase service;

    /// 나의 좋아요 방 목록
    @CheckLogin
    @GetMapping("/likes")
    public ApiResponse<List<UnityTypeLikeResponse>> getLikeComplexes(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {

        /// 서비스 호출
        var response = service.getComplexesLikes(principalDetails.getId());

        /// 리턴
        return ApiResponse.ok(response);
    }

    /// 상세 조회
    @GetMapping("/{complexId}")
    public ApiResponse<ComplexDetailResponse> getComplex(
            @PathVariable String complexId,
            @RequestParam String pinPointId) throws UnsupportedEncodingException {

        /// 서비스 호출
        var response = service.getComplex(complexId, pinPointId);

        /// 리턴
        return ApiResponse.ok(response);
    }

    /// 방 타입 목록 조회
    @GetMapping("/unit/{complexId}")
    public ApiResponse<List<UnitTypeResponse>> getComplexUnitTypes(
            @PathVariable String complexId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {

        /// 서비스 호출 (로그인하지 않은 경우 userId는 null)
        var userId = (principalDetails != null) ? principalDetails.getId() : null;
        var response = service.getComplexUnitTypes(complexId, userId);

        /// 리턴
        return ApiResponse.ok(response);
    }

    /// 대중교통 시뮬레이터
    @GetMapping("/transit/{complexId}")
    public ApiResponse<List<DistanceResponse>> distance(
            @PathVariable String complexId,
            @RequestParam String pinPointId) throws UnsupportedEncodingException {

        /// 서비스 호출
        var response = service.getDistance(complexId, pinPointId);

        /// 리턴
        return ApiResponse.ok(response);
    }

}
