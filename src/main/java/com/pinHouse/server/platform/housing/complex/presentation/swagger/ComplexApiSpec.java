package com.pinHouse.server.platform.housing.complex.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.DepositResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.DistanceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Tag(name = "임대주택 정보 조회 API", description = "보증금 및 거리를 계산하는 API입니다.")
public interface ComplexApiSpec {

    /// 임대주택 시뮬레이터
    @Operation(
            summary = "임대주택 예산 시뮬레이터API",
            description = "임대주택 ID로, 예산의 변동을 조회하는 API 입니다."
    )
    ApiResponse<DepositResponse> deposit(

            @Parameter(example = "18384", description = "공고 ID")
            @PathVariable String complexId,

            @Parameter(example = "26A", description = "주거 타입")
            @RequestParam String housingType,

            @Parameter(example = "0.001", description = "변환율")
            @RequestParam double percentage);

    /// 거리 시뮬레이터
    @Operation(
            summary = "거리 시뮬레이터 API",
            description = "임대주택 ID와 핀포인트 ID를 통해 계산을 진행합니다.")
    ApiResponse<List<DistanceResponse>> distance(
            @PathVariable String complexId,
            @RequestParam Long pinPointId) throws UnsupportedEncodingException;


}
