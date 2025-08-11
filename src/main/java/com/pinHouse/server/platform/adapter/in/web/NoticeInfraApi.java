package com.pinHouse.server.platform.adapter.in.web;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.application.in.NoticeInfraUseCase;
import com.pinHouse.server.platform.domain.notice.NoticeInfra;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notices/infra")
@RequiredArgsConstructor()
public class NoticeInfraApi {

    private final NoticeInfraUseCase service;


    /// 주변 인프라 조회
    @GetMapping("/{noticeId}")
    public ApiResponse<NoticeInfra> showNotice(
            @PathVariable String noticeId
    ) {

        /// 서비스 계층
        NoticeInfra noticeInfra = service.getNoticeInfraById(noticeId);

        /// 응답
        return ApiResponse.ok(noticeInfra);

    }
}
