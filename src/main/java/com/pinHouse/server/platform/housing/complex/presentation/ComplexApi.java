package com.pinHouse.server.platform.housing.complex.presentation;

import com.pinHouse.server.core.aop.CheckLogin;
import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.response.DistanceResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.result.PathResult;
import com.pinHouse.server.platform.housing.complex.application.usecase.ComplexUseCase;
import com.pinHouse.server.platform.housing.complex.application.dto.response.ComplexDetailResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.response.DepositResponse;
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
            @PathVariable String complexId
    ) {

        /// 서비스 호출
        var response = service.getComplex(complexId);

        /// 리턴
        return ApiResponse.ok(response);
    }

    /// 예산 시뮬레이터
    @GetMapping("/deposit/{complexId}")
    public ApiResponse<DepositResponse> deposit(
            @PathVariable String complexId,
            @RequestParam String housingType,
            @RequestParam double percentage) {

        /// 서비스 호출
        var response = service.getLeaseByPercent(complexId, housingType, percentage);

        /// 리턴
        return ApiResponse.ok(response);
    }

    /// 간편 대중교통 시뮬레이터
    @GetMapping("/transit/easy/{complexId}")
    public ApiResponse<DistanceResponse> distanceEasy(
            @PathVariable String complexId,
            @RequestParam String pinPointId) throws UnsupportedEncodingException {

        /// 서비스 호출
        var response = service.getEasyDistance(complexId, pinPointId);

        /// 리턴
        return ApiResponse.ok(response);
    }

    /// 대중교통 시뮬레이터
    @GetMapping("/transit/full/{complexId}")
    public ApiResponse<PathResult> distance(
            @PathVariable String complexId,
            @RequestParam String pinPointId) throws UnsupportedEncodingException {

        /// 서비스 호출
        var response = service.getDistance(complexId, pinPointId);

        /// 리턴
        return ApiResponse.ok(response);
    }

}
