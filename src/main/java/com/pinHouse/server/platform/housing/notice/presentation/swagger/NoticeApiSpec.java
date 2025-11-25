package com.pinHouse.server.platform.housing.notice.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.core.response.response.pageable.SliceRequest;
import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.ComplexFilterResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailFilterRequest;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailFilteredResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListRequest;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.UnitTypeCompareResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.UnitTypeSortType;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
            summary = "공고 상세 조회 API (필터 적용)",
            description = "공고 ID를 기준으로 공고 상세 정보를 조회하며, 필터 조건에 따라 filtered와 nonFiltered 데이터를 분리하여 반환합니다."
    )
    ApiResponse<NoticeDetailFilteredResponse> getNotice(
            @Parameter(example = "18214")
            @PathVariable String noticeId,
            @RequestBody NoticeDetailFilterRequest request);

    /// 필터 정보 조회
    @Operation(
            summary = "공고의 단지 필터링 정보 조회 API",
            description = "공고에 포함된 단지들의 지역, 가격, 면적 필터링 정보를 제공합니다. 프론트엔드 필터 UI 구성에 사용됩니다."
    )
    ApiResponse<ComplexFilterResponse> getComplexFilters(
            @Parameter(description = "공고 ID", example = "18214")
            @PathVariable String noticeId
    );

    /// 필터 조건에 맞는 단지 개수 조회
    @Operation(
            summary = "공고의 필터 조건에 맞는 단지 개수 조회 API",
            description = "필터 조건(거리, 지역, 비용, 면적, 인프라)에 맞는 단지가 몇 개 있는지 반환합니다. " +
                    "거리는 시간(분) 기반으로 평균 속도 15km/h로 계산됩니다. " +
                    "지역은 county(시/구) 기반으로 필터링됩니다. " +
                    "면적은 typeCode가 존재하는 단지를 필터링합니다."
    )
    ApiResponse<Integer> countFilteredComplexes(
            @Parameter(description = "공고 ID", example = "18214")
            @PathVariable String noticeId,

            @RequestBody NoticeDetailFilterRequest request
    );

    /// 유닛타입 비교
    @Operation(
            summary = "공고의 모든 유닛타입(방) 비교 API",
            description = "공고에 포함된 모든 유닛타입의 상세 특징을 조회하고 정렬합니다. " +
                    "보증금 낮은 순(DEPOSIT_ASC) 또는 평수 넓은 순(AREA_DESC)으로 정렬 가능합니다. " +
                    "면적, 비용, 공급호수, 단지 정보, 주변 인프라, 핀포인트 기준 거리, 좋아요 여부 등 모든 특징을 반환합니다. " +
                    "로그인한 사용자의 경우 각 유닛타입의 좋아요 여부가 포함됩니다."
    )
    ApiResponse<UnitTypeCompareResponse> compareUnitTypes(
            @Parameter(description = "공고 ID", example = "18214")
            @PathVariable String noticeId,

            @Parameter(description = "핀포인트 ID", example = "4dff2ba3-3232-4674-bddd-803ca06429ff")
            @RequestParam(required = false) String pinPointId,

            @Parameter(description = "정렬 기준 (DEPOSIT_ASC: 보증금 낮은 순, AREA_DESC: 평수 넓은 순)",
                       example = "DEPOSIT_ASC")
            @RequestParam(required = false, defaultValue = "보증금 낮은 순") UnitTypeSortType sortType,

            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

}
