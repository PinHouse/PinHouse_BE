package com.pinHouse.server.platform.home.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.core.response.response.pageable.SliceRequest;
import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.home.application.dto.HomeNoticeListResponse;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchFilterType;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchResultResponse;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchSortType;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "홈 API", description = "홈 화면 관련 API 입니다")
public interface HomeApiSpec {

    @Operation(
            summary = "마감임박공고 조회 API (PinPoint 지역 기반) - 로그인 필수",
            description = "PinPoint의 지역을 기반으로 마감임박순으로 정렬된 공고 목록을 조회하는 API 입니다. " +
                    "PinPoint의 주소에서 광역 단위(시/도)를 추출하여 해당 지역의 모집중인 공고만 조회합니다. " +
                    "본인의 PinPoint만 사용 가능하며, 다른 사용자의 PinPoint ID를 사용하면 400 에러가 발생합니다. " +
                    "응답 구조: 공통 지역(region) + 공고 목록 배열(content) + 페이징 정보(hasNext, totalElements). " +
                    "SliceResponse와 유사한 구조이지만 region 필드가 추가되었습니다."
    )
    ApiResponse<HomeNoticeListResponse> getDeadlineApproachingNotices(
            @Parameter(description = "PinPoint ID", example = "83ec36ce-8fc1-4f62-8983-397c2729fc22")
            @RequestParam String pinpointId,

            SliceRequest sliceRequest,

            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(
            summary = "통합 검색 API",
            description = "공고 제목 및 타겟 그룹을 기반으로 검색하는 통합 검색 API 입니다. " +
                    "키워드를 입력하면 공고 제목과 모집 대상에서 검색하여 결과를 반환합니다. " +
                    "정렬 방식과 공고 상태 필터를 적용할 수 있으며, 로그인한 사용자의 경우 좋아요 정보가 포함됩니다."
    )
    ApiResponse<SliceResponse<NoticeSearchResultResponse>> searchNoticesIntegrated(
            @Parameter(description = "검색 키워드", example = "청년")
            @RequestParam String q,

            SliceRequest sliceRequest,

            @Parameter(description = "정렬 방식 (LATEST: 최신공고순, END: 마감임박순)", example = "LATEST")
            @RequestParam(required = false, defaultValue = "LATEST") NoticeSearchSortType sortType,

            @Parameter(description = "공고 상태 (ALL: 전체, RECRUITING: 모집중)", example = "ALL")
            @RequestParam(required = false, defaultValue = "ALL") NoticeSearchFilterType status,

            @AuthenticationPrincipal PrincipalDetails principalDetails
    );
}
