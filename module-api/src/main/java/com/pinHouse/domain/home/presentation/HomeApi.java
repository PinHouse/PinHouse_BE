package com.pinHouse.domain.home.presentation;

import com.pinHouse.common.aop.CheckLogin;
import com.pinHouse.common.auth.CurrentUserId;
import com.pinHouse.common.response.ApiResponse;
import com.pinHouse.common.response.pageable.SliceRequest;
import com.pinHouse.domain.home.application.dto.HomeNoticeListResponse;
import com.pinHouse.domain.home.application.dto.HomeSearchCategoryPageResponse;
import com.pinHouse.domain.home.application.dto.HomeSearchCategoryType;
import com.pinHouse.domain.home.application.dto.HomeSearchOverviewResponse;
import com.pinHouse.domain.home.application.dto.NoticeCountResponse;
import com.pinHouse.domain.home.application.usecase.HomeUseCase;
import com.pinHouse.domain.home.presentation.swagger.HomeApiSpec;
import com.pinHouse.domain.search.application.dto.PopularKeywordResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

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
            @CurrentUserId(required = true) UUID userId
    ) {
        // @CheckLogin에 의해 principalDetails는 항상 non-null

        // 서비스 호출
        HomeNoticeListResponse response = homeService.getDeadlineApproachingNotices(
                pinpointId,
                sliceRequest,
                userId
        );

        return ApiResponse.ok(response);
    }

    /**
     * 홈 통합 검색 미리보기 (섹션별 5개)
     * GET /v1/home/search/overview?q=키워드
     */
    @Override
    @GetMapping("/search/overview")
    public ApiResponse<HomeSearchOverviewResponse> searchOverview(
            @RequestParam String q,
            @CurrentUserId UUID userId
    ) {
        HomeSearchOverviewResponse response = homeService.searchHomeOverview(q, userId);
        return ApiResponse.ok(response);
    }

    /**
     * 홈 통합 검색 카테고리별 조회 (더보기)
     * GET /v1/home/search/category?type=NOTICE&q=키워드&page=1&offSet=20
     */
    @Override
    @GetMapping("/search/category")
    public ApiResponse<HomeSearchCategoryPageResponse> searchByCategory(
            @RequestParam HomeSearchCategoryType type,
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page,
            @CurrentUserId UUID userId
    ) {
        HomeSearchCategoryPageResponse response = homeService.searchHomeByCategory(
                type,
                q,
                page,
                userId
        );
        return ApiResponse.ok(response);
    }

    /**
     * 홈 인기 검색어 조회
     * GET /v1/home/search/popular?limit=10
     */
    @Override
    @GetMapping("/search/popular")
    public ApiResponse<List<PopularKeywordResponse>> getHomePopularKeywords(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<PopularKeywordResponse> response = homeService.getHomePopularKeywords(limit);
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
            @CurrentUserId(required = true) UUID userId
    ) {
        // @CheckLogin에 의해 principalDetails는 항상 non-null

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
            @CurrentUserId(required = true) UUID userId
    ) {
        // @CheckLogin에 의해 principalDetails는 항상 non-null

        // 서비스 호출
        HomeNoticeListResponse response = homeService.getRecommendedNoticesByDiagnosis(
                sliceRequest,
                userId
        );

        return ApiResponse.ok(response);
    }
}
