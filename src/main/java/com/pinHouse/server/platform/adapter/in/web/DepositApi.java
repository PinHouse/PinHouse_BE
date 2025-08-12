package com.pinHouse.server.platform.adapter.in.web;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.adapter.in.web.dto.response.NoticeSupplyDTO;
import com.pinHouse.server.platform.adapter.in.web.swagger.DepositApiSpec;
import com.pinHouse.server.platform.application.in.NoticeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 예산 시뮬레이터 관련 API 입니다
 */
@RestController
@RequestMapping("/api/v1/notices/deposit")
@RequiredArgsConstructor
public class DepositApi implements DepositApiSpec {

    private final NoticeUseCase noticeService;

    @PutMapping("/{noticeId}")
    public ApiResponse<NoticeSupplyDTO.NoticeLeaseOptionResponse> update(
            @PathVariable String noticeId,
            @RequestParam String housingType,
            @RequestParam double percentage) {

        /// 서비스 호출
        NoticeSupplyDTO.NoticeLeaseOptionResponse lease = noticeService.getLeaseByPercent(noticeId, housingType, percentage);

        return ApiResponse.ok(lease);

    }

}
