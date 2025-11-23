package com.pinHouse.server.platform.housing.notice.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.core.response.response.pageable.SliceRequest;
import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailFilterRequest;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListRequest;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@Tag(name = "공고 API", description = "공고 기능 API 입니다")
public interface NoticeApiSpec {

    /// 목록 조회
    @Operation(
            summary = "좋아요 한 공고 목록 조회",
            description = "좋아요 누른 공고를 조회하는 API 입니다."
    )
    ApiResponse<List<NoticeListResponse>> getLikeNotices(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(
            summary = "공고 목록 조회 API",
            description = "최신 날짜 기준으로, 공고 목록을 조회하는 API 입니다. 로그인한 사용자의 경우 좋아요 정보가 포함됩니다."
    )
    ApiResponse<SliceResponse<NoticeListResponse>> getNotices(
            @RequestBody NoticeListRequest request,
            SliceRequest sliceRequest,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );


    /// 상세 조회
    @Operation(
            summary = "공고 상세 조회 API",
            description = "공고 ID를 기준으로, 공고 상세 정보를 조회하는 API 입니다."
    )
    ApiResponse<NoticeDetailResponse> getNotice(
            @Parameter(example = "18442")
            @PathVariable String noticeId,
            @RequestBody NoticeDetailFilterRequest request);

}
