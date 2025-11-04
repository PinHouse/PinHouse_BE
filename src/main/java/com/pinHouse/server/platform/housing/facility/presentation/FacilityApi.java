package com.pinHouse.server.platform.housing.facility.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.housing.facility.application.dto.NoticeFacilityListResponse;
import com.pinHouse.server.platform.housing.facility.presentation.swagger.FacilityApiSpec;
import com.pinHouse.server.platform.housing.facility.application.usecase.FacilityUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/complexes/infra")
@RequiredArgsConstructor
public class FacilityApi implements FacilityApiSpec {

    /// 서비스 의존성
    private final FacilityUseCase service;

    /// 주변 인프라 조회
    @GetMapping("/{complexId}")
    public ApiResponse<NoticeFacilityListResponse> showNotice(
            @PathVariable String complexId) {

        /// 서비스 계층
        var response = service.getNearFacilities(complexId);

        /// 리턴
        return ApiResponse.ok(response);
    }

    /// 원하는 인프라를 바탕으로 많이 존재하는 지역을 설정
    @GetMapping()
    public ApiResponse<List<ComplexDocument>> showNoticeList(
            @RequestParam List<FacilityType> facilityTypes
    ) {

        /// 서비스 계층
        var response = service.getComplexes(facilityTypes);

        /// 리턴
        return ApiResponse.ok(response);
    }

}
