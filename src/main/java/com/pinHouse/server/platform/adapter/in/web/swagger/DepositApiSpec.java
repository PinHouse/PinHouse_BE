package com.pinHouse.server.platform.adapter.in.web.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.adapter.in.web.dto.response.NoticeSupplyDTO;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "예산 시뮬레이터 API", description = "보증금/최대전환월세를 계산하는 API입니다.")
public interface DepositApiSpec {

    @PutMapping("/{noticeId}")
    ApiResponse<NoticeSupplyDTO.NoticeLeaseOptionResponse> update(

            @Parameter(example = "18384", description = "공고 ID")
            @PathVariable String noticeId,

            @Parameter(example = "26A", description = "주거 타입")
            @RequestParam String housingType,

            @Parameter(example = "0.001", description = "변환율")
            @RequestParam double percentage);

}
