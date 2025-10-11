package com.pinHouse.server.platform.housing.notice.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.core.response.response.pageable.PageRequest;
import com.pinHouse.server.core.response.response.pageable.PageResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.platform.housing.notice.presentation.swagger.NoticeApiSpec;
import com.pinHouse.server.platform.housing.notice.application.usecase.NoticeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeApi implements NoticeApiSpec {

    private final NoticeUseCase noticeService;

    /**
     * 최신 공고 API
     * @param request   페이징 요청
     * @return
     */
    @GetMapping
    public ApiResponse<PageResponse<NoticeListResponse>> getNotices(PageRequest request) {

        /// 서비스 계층
        Page<NoticeListResponse> notices = noticeService.getNotices(request);

        /// PageDTO 감싸기
        PageResponse<NoticeListResponse> response = new PageResponse<>(notices.getContent(), request, notices.getTotalElements());

        return ApiResponse.ok(response);
    }

    @GetMapping("/{noticeId}")
    public ApiResponse<NoticeDetailResponse> showNotice(
            @PathVariable String noticeId
    ) {

        /// 서비스 계층
        return ApiResponse.ok(noticeService.getNoticeById(noticeId));

    }

}
