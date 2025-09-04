package com.pinHouse.server.platform.facility.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.notice.application.dto.response.InfraDTO;
import com.pinHouse.server.platform.notice.application.dto.response.NoticeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "공고 주변 인프라 API", description = "인프라를 바탕으로 조회하는 API 입니다.")
public interface FacilityApiSpec {

    @Operation(
            summary = "공고 주변 인프라 조회 API",
            description = "공고 주변 3KM내 존재하는 인프라를 확인하는 API입니다."
    )
    @GetMapping("/{noticeId}")
    ApiResponse<InfraDTO.NoticeInfraResponse> showNotice(
            @Parameter(example = "18407")
            @PathVariable String noticeId);

    @Operation(
            summary = "인프라에 따른 공고 조회 API",
            description = "해당 인프라가 2개 이상 존재하는 공고를 조회하는 API입니다."
    )
    @GetMapping("/type")
    ApiResponse<List<NoticeDTO.NoticeListResponse>> showNoticeList(
            @RequestParam List<FacilityType> facilityTypes
    );
}
