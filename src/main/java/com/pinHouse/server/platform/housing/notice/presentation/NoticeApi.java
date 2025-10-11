package com.pinHouse.server.platform.housing.notice.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.core.response.response.pageable.PageRequest;
import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.platform.housing.notice.presentation.swagger.NoticeApiSpec;
import com.pinHouse.server.platform.housing.notice.application.usecase.NoticeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/notices")
@RequiredArgsConstructor
public class NoticeApi implements NoticeApiSpec {

    /// 의존성 주입
    private final NoticeUseCase noticeService;

    /// 공고 목록 조회
    @GetMapping
    public ApiResponse<SliceResponse<NoticeListResponse>> getNotices(PageRequest sliceRequest) {

        /// 서비스 계층
        var response = noticeService.getNotices(sliceRequest);

        /// 리턴
        return ApiResponse.ok(response);
    }

    /// 공고 상세 조회
    @GetMapping("/{noticeId}")
    public ApiResponse<NoticeDetailResponse> showNotice(
            @PathVariable String noticeId
    ) {

        /// 서비스 계층
        NoticeDetailResponse response = noticeService.getNoticeById(noticeId);

        /// 리턴
        return ApiResponse.ok(response);

    }

}
