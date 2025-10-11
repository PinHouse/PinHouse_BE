package com.pinHouse.server.platform.housing.notice.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "공고 API", description = "공고 기능 API 입니다")
public interface NoticeApiSpec {

    @Operation(
            summary = "공고 목록 조회 API",
            description = "최신 날짜 기준으로, 공고 목록을 조회하는 API 입니다."
    )
    ApiResponse<List<NoticeListResponse>> getNotices() ;


    @Operation(
            summary = "공고 상세 조회 API",
            description = "공고 ID를 기준으로, 공고 상세 정보를 조회하는 API 입니다."
    )
    ApiResponse<NoticeDetailResponse> getNotice(
            @Parameter(example = "18442")
            @PathVariable String noticeId);
}
