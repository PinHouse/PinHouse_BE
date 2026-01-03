package com.pinHouse.server.platform.housing.notice.application.dto;

import com.pinHouse.server.core.util.DateUtil;
import com.pinHouse.server.platform.housing.complex.application.dto.response.ComplexDetailResponse;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.facility.application.dto.NoticeFacilityListResponse;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.Map;

/**
 * 공고 상세 조회 응답 (필터링 적용)
 * filtered: 필터 조건을 만족하는 데이터
 * nonFiltered: 필터 조건을 만족하지 않는 데이터
 */
@Builder
@Schema(name = "[응답][공고] 공고 상세 조회 (필터 적용)", description = "필터링된 데이터와 필터링되지 않은 데이터를 구분하여 반환")
public record NoticeDetailFilteredResponse(
        @Schema(description = "공고 기본 정보")
        NoticeBasicInfo basicInfo,

        @Schema(description = "필터 조건을 만족하는 데이터")
        NoticeDetailData filtered,

        @Schema(description = "필터 조건을 만족하지 않는 데이터")
        NoticeDetailData nonFiltered
) {

    /**
     * 정적 팩토리 메서드 - 이미 필터링된 데이터를 받아서 응답 생성
     */
    public static NoticeDetailFilteredResponse from(
            NoticeDocument notice,
            List<ComplexDocument> filteredComplexes,
            List<ComplexDocument> nonFilteredComplexes,
            Map<String, NoticeFacilityListResponse> facilityMap
    ) {
        // 1. 기본 정보 생성
        NoticeBasicInfo basicInfo = NoticeBasicInfo.from(notice);

        // 2. 필터링된 데이터로 응답 생성
        NoticeDetailData filtered = NoticeDetailData.from(filteredComplexes, facilityMap);
        NoticeDetailData nonFiltered = NoticeDetailData.from(nonFilteredComplexes, facilityMap);

        return NoticeDetailFilteredResponse.builder()
                .basicInfo(basicInfo)
                .filtered(filtered)
                .nonFiltered(nonFiltered)
                .build();
    }

    /**
     * 정적 팩토리 메서드 - 거리 정보 포함
     */
    public static NoticeDetailFilteredResponse from(
            NoticeDocument notice,
            List<ComplexDocument> filteredComplexes,
            List<ComplexDocument> nonFilteredComplexes,
            Map<String, NoticeFacilityListResponse> facilityMap,
            Map<String, Integer> totalTimeMap
    ) {
        // 1. 기본 정보 생성
        NoticeBasicInfo basicInfo = NoticeBasicInfo.from(notice);

        // 2. 필터링된 데이터로 응답 생성 (거리 정보 포함)
        NoticeDetailData filtered = NoticeDetailData.from(filteredComplexes, facilityMap, totalTimeMap);
        NoticeDetailData nonFiltered = NoticeDetailData.from(nonFilteredComplexes, facilityMap, totalTimeMap);

        return NoticeDetailFilteredResponse.builder()
                .basicInfo(basicInfo)
                .filtered(filtered)
                .nonFiltered(nonFiltered)
                .build();
    }

    /**
     * 공고 기본 정보 (필터와 무관한 정보)
     */
    @Builder
    public record NoticeBasicInfo(
            @Schema(description = "공고ID", example = "101")
            String id,

            @Schema(description = "임대유형", example = "영구임대")
            String type,

            @Schema(description = "주택유형", example = "아파트")
            String housingType,

            @Schema(description = "공급주체", example = "LH")
            String supplier,

            @Schema(description = "공고명", example = "2025 청년 행복주택 공고")
            String name,

            @Schema(description = "모집일정", example = "2025년 10월 ~ 11월")
            String period,

            @Schema(description = "대상 계층", example = "[\"청년\", \"신혼부부\"]")
            List<String> targetGroups
    ) {
        public static NoticeBasicInfo from(NoticeDocument notice) {
            String period = DateUtil.formatDate(notice.getApplyStart(), notice.getApplyEnd());
            return NoticeBasicInfo.builder()
                    .id(notice.getId())
                    .name(notice.getTitle())
                    .supplier(notice.getAgency())
                    .period(period)
                    .type(notice.getSupplyType())
                    .housingType(notice.getHouseType())
                    .targetGroups(notice.getTargetGroups())
                    .build();
        }
    }

    /**
     * 필터 적용 데이터 섹션
     */
    @Builder
    public record NoticeDetailData(
            @Schema(description = "해당 섹션의 임대주택 개수", example = "3")
            long totalCount,

            @Schema(description = "임대주택 목록")
            List<ComplexDetailResponse> complexes
    ) {
        public static NoticeDetailData from(
                List<ComplexDocument> complexes,
                Map<String, NoticeFacilityListResponse> facilityMap
        ) {
            List<ComplexDetailResponse> responses = ComplexDetailResponse.from(complexes, facilityMap);
            return NoticeDetailData.builder()
                    .totalCount(responses.size())
                    .complexes(responses)
                    .build();
        }

        public static NoticeDetailData from(
                List<ComplexDocument> complexes,
                Map<String, NoticeFacilityListResponse> facilityMap,
                Map<String, Integer> totalTimeMap
        ) {
            List<ComplexDetailResponse> responses = ComplexDetailResponse.from(complexes, facilityMap, totalTimeMap);
            return NoticeDetailData.builder()
                    .totalCount(responses.size())
                    .complexes(responses)
                    .build();
        }
    }
}
