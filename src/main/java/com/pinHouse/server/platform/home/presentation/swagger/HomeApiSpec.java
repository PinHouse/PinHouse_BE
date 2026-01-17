package com.pinHouse.server.platform.home.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.core.response.response.pageable.SliceRequest;
import com.pinHouse.server.platform.home.application.dto.HomeNoticeListResponse;
import com.pinHouse.server.platform.home.application.dto.HomeSearchCategoryPageResponse;
import com.pinHouse.server.platform.home.application.dto.HomeSearchCategoryType;
import com.pinHouse.server.platform.home.application.dto.HomeSearchOverviewResponse;
import com.pinHouse.server.platform.home.application.dto.NoticeCountResponse;
import com.pinHouse.server.platform.search.application.dto.PopularKeywordResponse;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "홈 API", description = "홈 화면 관련 API 입니다")
public interface HomeApiSpec {

    @Operation(
            summary = "마감임박공고 조회 API",
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
            summary = "홈 통합 검색 미리보기",
            description = "공고명, 단지명, 모집대상, 지역, 주택유형을 대상으로 상위 5개씩 미리보기 결과를 반환합니다."
    )
    ApiResponse<HomeSearchOverviewResponse> searchOverview(
            @Parameter(description = "검색 키워드", example = "청년")
            @RequestParam String q,

            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(
            summary = "홈 통합 검색 카테고리별 조회",
            description = "홈 통합 검색의 더보기 API로, 카테고리별로 별도 페이징하여 결과를 반환합니다."
    )
    ApiResponse<HomeSearchCategoryPageResponse> searchByCategory(
            @Parameter(description = "카테고리 타입", example = "NOTICE")
            @RequestParam HomeSearchCategoryType type,

            @Parameter(description = "검색 키워드", example = "청년")
            @RequestParam String q,

            @Parameter(description = "페이지 (1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") int page,

            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(
            summary = "홈 인기 검색어 조회",
            description = "홈 통합 검색에서 많이 검색된 키워드를 조회합니다."
    )
    ApiResponse<java.util.List<PopularKeywordResponse>> getHomePopularKeywords(
            @Parameter(description = "조회 개수", example = "10")
            @RequestParam(defaultValue = "10") int limit
    );

    @Operation(
            summary = "핀포인트 기준 공고 개수 조회 API",
            description = "핀포인트를 기준으로 최대 이동 시간(분) 내에 위치한 공고의 개수를 조회하는 API 입니다. " +
                    "대중교통 평균 속도(15km/h)를 기준으로 반경을 계산하여 해당 범위 내 단지들의 고유 공고 개수를 반환합니다. " +
                    "본인의 PinPoint만 사용 가능하며, 다른 사용자의 PinPoint ID를 사용하면 400 에러가 발생합니다."
    )
    ApiResponse<NoticeCountResponse> getNoticeCountWithinTravelTime(
            @Parameter(description = "핀포인트 ID", example = "83ec36ce-8fc1-4f62-8983-397c2729fc22")
            @RequestParam String pinPointId,

            @Parameter(description = "최대 이동 시간 (분)", example = "30")
            @RequestParam int maxTime,

            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(
            summary = "진단 기반 추천 공고 조회 API",
            description = "사용자의 최근 청약 진단 결과를 기반으로 추천 공고를 조회하는 API입니다. " +
                    "진단 결과의 신청가능한 임대주택 유형(availableRentalTypes)을 기준으로 공고를 필터링하며, " +
                    "마감임박순으로 정렬됩니다. 모든 공고 상태(모집중 + 마감)를 포함합니다. " +
                    "진단 기록이 없는 경우 404 에러가 발생합니다. " +
                    "진단 결과 자격이 없는 경우(해당 없음) 200 OK와 함께 빈 리스트를 반환합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공 (진단 결과에 맞는 공고가 없어도 200 반환)",
                    content = @Content(schema = @Schema(implementation = HomeNoticeListResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "진단 기록을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (로그인 필요)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ApiResponse<HomeNoticeListResponse> getRecommendedNoticesByDiagnosis(
            SliceRequest sliceRequest,

            @AuthenticationPrincipal PrincipalDetails principalDetails
    );
}
