package com.pinHouse.server.platform.adapter.in.web.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.adapter.in.web.dto.response.InfraDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "공고 주변 인프라 API", description = "인프라를 바탕으로 조회하는 API 입니다.")
public interface NoticeInfraApiSpec {

    @Operation(
            summary = "공고 주변 인프라 조회 API",
            description = "공고 주변 3KM내 존재하는 인프라를 확인하는 API입니다."
    )
    @GetMapping("/{noticeId}")
    ApiResponse<InfraDTO.NoticeInfraResponse> showNotice(
            @Parameter(example = "18407")
            @PathVariable String noticeId);
}
