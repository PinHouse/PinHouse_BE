package com.pinHouse.server.platform.housing.notice.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.core.response.response.pageable.SliceRequest;
import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.SortType;
import com.pinHouse.server.platform.housing.notice.application.usecase.NoticeUseCase;
import com.pinHouse.server.platform.housing.notice.presentation.swagger.NoticeApiSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/notices")
@RequiredArgsConstructor
public class NoticeApi implements NoticeApiSpec {

    /// 의존성 주입
    private final NoticeUseCase service;

    /// 공고 목록 조회
    @GetMapping
    public ApiResponse<SliceResponse<NoticeListResponse>> getNotices(
            @RequestParam SortType sort,
            SliceRequest sliceRequest
    ) {

        /// 서비스 계층
        var response = service.getNotices(sort, sliceRequest);

        /// 리턴
        return ApiResponse.ok(response);
    }

    /// 공고 상세 조회
    @GetMapping("/{noticeId}")
    public ApiResponse<NoticeDetailResponse> getNotice(@PathVariable String noticeId) {

        /// 서비스 계층
        var response = service.getNotice(noticeId);

        /// 리턴
        return ApiResponse.ok(response);
    }
}
