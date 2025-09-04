package com.pinHouse.server.platform.notice.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.notice.application.dto.response.InfraDTO;
import com.pinHouse.server.platform.notice.application.dto.response.NoticeDTO;
import com.pinHouse.server.platform.notice.presentation.swagger.NoticeInfraApiSpec;
import com.pinHouse.server.platform.notice.application.usecase.NoticeInfraUseCase;
import com.pinHouse.server.platform.notice.domain.Notice;
import com.pinHouse.server.platform.notice.domain.NoticeInfra;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notices/infra")
@RequiredArgsConstructor
public class NoticeInfraApi implements NoticeInfraApiSpec {

    /// 서비스 의존성
    private final NoticeInfraUseCase service;

    /// 주변 인프라 조회
    @GetMapping("/{noticeId}")
    public ApiResponse<InfraDTO.NoticeInfraResponse> showNotice(
            @PathVariable String noticeId) {

        /// 서비스 계층
        NoticeInfra noticeInfra = service.getNoticeInfraById(noticeId);

        /// DTO 수정
        InfraDTO.NoticeInfraResponse response = InfraDTO.NoticeInfraResponse.from(noticeInfra);

        /// 응답
        return ApiResponse.ok(response);
    }

    /// 원하는 인프라를 바탕으로 많이 존재하는 지역을 설정
    @GetMapping("/type")
    public ApiResponse<List<NoticeDTO.NoticeListResponse>> showNoticeList(
            @RequestParam List<FacilityType> facilityTypes
    ) {
        // 1. 시설 타입별로 지역(행정동 등)별 시설 수 집계
        List<Notice> notices = service.getNoticesByInfraTypesWithAllMinCount(facilityTypes);

        // 4. 응답 DTO 구성
        List<NoticeDTO.NoticeListResponse> responses = NoticeDTO.NoticeListResponse.from(notices);

        return ApiResponse.ok(responses);
    }

}
