package com.pinHouse.server.platform.housing.notice.presentation;

import com.pinHouse.server.core.aop.CheckLogin;
import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.core.response.response.pageable.SliceRequest;
import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailRequest;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListRequest;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.platform.housing.notice.application.usecase.NoticeUseCase;
import com.pinHouse.server.platform.housing.notice.presentation.swagger.NoticeApiSpec;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/v1/notices")
@RequiredArgsConstructor
public class NoticeApi implements NoticeApiSpec {

    /// 의존성 주입
    private final NoticeUseCase service;

    /// 공고 목록 조회
    @PostMapping
    public ApiResponse<SliceResponse<NoticeListResponse>> getNotices(
            @RequestBody NoticeListRequest request,
            SliceRequest sliceRequest
    ) {

        /// 서비스 계층
        var response = service.getNotices(request, sliceRequest);

        /// 리턴
        return ApiResponse.ok(response);
    }

    /// 나의 좋아요 공고 목록 조회
    @CheckLogin
    @GetMapping("/likes")
    public ApiResponse<List<NoticeListResponse>> getLikeNotices(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {

        /// 서비스 호출
        var response = service.getNoticesLike(principalDetails.getId());

        /// 리턴
        return ApiResponse.ok(response);
    }

    /// 공고 상세 조회
    @PostMapping("/{noticeId}")
    public ApiResponse<NoticeDetailResponse> getNotice(
            @PathVariable String noticeId,
            @RequestBody NoticeDetailRequest request) {

        /// 서비스 계층
        var response = service.getNotice(noticeId, request);

        /// 리턴
        return ApiResponse.ok(response);
    }
}
