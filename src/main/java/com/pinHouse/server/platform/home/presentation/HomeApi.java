package com.pinHouse.server.platform.home.presentation;

import com.pinHouse.server.core.aop.CheckLogin;
import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.core.response.response.pageable.SliceRequest;
import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.home.application.dto.HomeNoticeListResponse;
import com.pinHouse.server.platform.home.application.dto.NoticeCountResponse;
import com.pinHouse.server.platform.home.application.usecase.HomeUseCase;
import com.pinHouse.server.platform.home.presentation.swagger.HomeApiSpec;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchFilterType;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchResultResponse;
import com.pinHouse.server.platform.search.application.dto.NoticeSearchSortType;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 홈 화면 API
 */
@Slf4j
@RestController
@RequestMapping("/v1/home")
@RequiredArgsConstructor
public class HomeApi implements HomeApiSpec {

    private final HomeUseCase homeService;

    /**
     * 마감임박공고 조회 (PinPoint 지역 기반)
     * GET /v1/home/deadline-approaching?pinpointId=xxx&page=1&offSet=20
     * 로그인 필수
     */
    @Override
    @CheckLogin
    @GetMapping("/notice")
    public ApiResponse<HomeNoticeListResponse> getDeadlineApproachingNotices(
            @RequestParam String pinpointId,
            SliceRequest sliceRequest,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        // @CheckLogin에 의해 principalDetails는 항상 non-null
        UUID userId = principalDetails.getId();

        // 서비스 호출
        HomeNoticeListResponse response = homeService.getDeadlineApproachingNotices(
                pinpointId,
                sliceRequest,
                userId
        );

        return ApiResponse.ok(response);
    }

    /**
     * 통합 검색 (공고 제목 및 타겟 그룹 기반)
     * GET /v1/home/search?q=키워드&page=1&offSet=20&sortType=LATEST&status=ALL
     */
    @Override
    @GetMapping("/search")
    public ApiResponse<SliceResponse<NoticeSearchResultResponse>> searchNoticesIntegrated(
            @RequestParam String q,
            SliceRequest sliceRequest,
            @RequestParam(required = false, defaultValue = "LATEST") NoticeSearchSortType sortType,
            @RequestParam(required = false, defaultValue = "ALL") NoticeSearchFilterType status,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        // 로그인하지 않은 경우 userId는 null
        UUID userId = (principalDetails != null) ? principalDetails.getId() : null;

        // 서비스 호출
        SliceResponse<NoticeSearchResultResponse> response = homeService.searchNoticesIntegrated(
                q,
                sliceRequest.page(),
                sliceRequest.offSet(),
                sortType,
                status,
                userId
        );

        return ApiResponse.ok(response);
    }

    /**
     * 핀포인트 기준 공고 개수 조회
     * GET /v1/home/notice-count?pinPointId=xxx&maxTime=30
     * 로그인 필수
     */
    @Override
    @CheckLogin
    @GetMapping("/notice-count")
    public ApiResponse<NoticeCountResponse> getNoticeCountWithinTravelTime(
            @RequestParam String pinPointId,
            @RequestParam int maxTime,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        // @CheckLogin에 의해 principalDetails는 항상 non-null
        UUID userId = principalDetails.getId();

        // 서비스 호출
        NoticeCountResponse response = homeService.getNoticeCountWithinTravelTime(
                pinPointId,
                maxTime,
                userId
        );

        return ApiResponse.ok(response);
    }

    /**
     * 진단 기반 추천 공고 조회
     * GET /v1/home/recommended-notices?page=1&offSet=20
     * 로그인 필수
     */
    @Override
    @CheckLogin
    @GetMapping("/recommended-notices")
    public ApiResponse<HomeNoticeListResponse> getRecommendedNoticesByDiagnosis(
            SliceRequest sliceRequest,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        // @CheckLogin에 의해 principalDetails는 항상 non-null
        UUID userId = principalDetails.getId();

        // 서비스 호출
        HomeNoticeListResponse response = homeService.getRecommendedNoticesByDiagnosis(
                sliceRequest,
                userId
        );

        return ApiResponse.ok(response);
    }
}
