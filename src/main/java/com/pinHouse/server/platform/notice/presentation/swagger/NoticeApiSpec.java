package com.pinHouse.server.platform.notice.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.core.response.response.pageable.PageRequest;
import com.pinHouse.server.core.response.response.pageable.PageResponse;
import com.pinHouse.server.platform.notice.application.dto.response.NoticeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "공고 API", description = "공고 기능 API 입니다")
public interface NoticeApiSpec {

    @Operation(
            summary = "공고 목록 조회 API",
            description = "최신 날짜 기준으로, 공고 목록을 조회하는 API 입니다."
    )
    ApiResponse<PageResponse<NoticeDTO.NoticeListResponse>> getNotices(PageRequest request);


    @Operation(
            summary = "공고 상세 조회 API",
            description = "공고 ID를 기준으로, 공고 상세 정보를 조회하는 API 입니다."
    )
    ApiResponse<NoticeDTO.NoticeDetailResponse> showNotice(
            @Parameter(example = "18384")
            @PathVariable String noticeId);
}
