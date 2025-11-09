package com.pinHouse.server.platform.housing.complex.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.response.ComplexDetailResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.response.DepositResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.response.DistanceResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.result.PathResult;
import com.pinHouse.server.platform.like.application.dto.UnityTypeLikeResponse;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Tag(name = "임대주택 정보 조회 API", description = "보증금 및 거리를 계산하는 API입니다.")
public interface ComplexApiSpec {

    /// 상세 조회
    @Operation(
            summary = "임대주택 상세 조회 API",
            description = "임대주택 ID로 상세 조회하는 API 입니다."
    )
    ApiResponse<ComplexDetailResponse> getComplex(
            @Parameter(example = "19231#37", description = "임대주택 ID")
            @PathVariable String complexId
    );


    @Operation(
            summary = "좋아요 한 임대주택 목록 조회",
            description = "좋아요 누른 임대주택을 조회하는 API 입니다."
    )
    ApiResponse<List<UnityTypeLikeResponse>> getLikeComplexes(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    /// 임대주택 시뮬레이터
    @Operation(
            summary = "임대주택 예산 시뮬레이터 API",
            description = "임대주택 ID로, 예산의 변동을 조회하는 API 입니다."
    )
    ApiResponse<DepositResponse> deposit(

            @Parameter(example = "19231#37", description = "임대주택 ID")
            @PathVariable String complexId,

            @Parameter(example = "46A", description = "주거 타입")
            @RequestParam String housingType,

            @Parameter(example = "0.001", description = "변환율")
            @RequestParam double percentage);

    /// 간편 거리 시뮬레이터
    @Operation(
            summary = "간편 거리 시뮬레이터 API",
            description = "임대주택 ID와 핀포인트 ID를 통해 계산을 진행합니다.")
    ApiResponse<DistanceResponse> distanceEasy(
            @Parameter(example = "19231#37", description = "시도 내 조회")
            @PathVariable String complexId,

            @Parameter(example = "4dff2ba3-3232-4674-bddd-803ca06429ff", description = "핀포인트 ID")
            @RequestParam String pinPointId) throws UnsupportedEncodingException;

    /// 거리 시뮬레이터
    @Operation(
            summary = "거리 시뮬레이터 API",
            description = "임대주택 ID와 핀포인트 ID를 통해 계산을 진행합니다.")
    ApiResponse<PathResult> distance(
            @Parameter(example = "19207#1", description = "시도 간 조회")
            @PathVariable String complexId,
            @Parameter(example = "4dff2ba3-3232-4674-bddd-803ca06429ff", description = "핀포인트 ID")
            @RequestParam String pinPointId) throws UnsupportedEncodingException;

}
