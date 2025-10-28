package com.pinHouse.server.platform.housing.facility.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.housing.facility.application.service.FacilityBatch;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("local")
@RequiredArgsConstructor
@RequestMapping("/v1/facility")
public class FacilityBatchApi {

    /// 서비스 의존성
    private final FacilityBatch service;

    /// 주변 인프라 조회
    @PostMapping("/batch")
    public ApiResponse<Void> batch() {

        /// 서비스 계층
        service.getFacilities();

        /// 리턴
        return ApiResponse.created();
    }


}
