package com.pinHouse.server.platform.housing.complex.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pinHouse.server.core.util.TimeFormatter;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import com.pinHouse.server.platform.housing.facility.application.dto.NoticeFacilityListResponse;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ComplexDetailResponse(
        @Schema(description = "임대주택 ID (복합키: noticeId#houseSn)", example = "notice123#house456")
        String id,

        @Schema(description = "단지명", example = "행복주택 1단지")
        String name,

        @Schema(description = "주소 (시/도 + 시/군/구)", example = "서울특별시 강남구")
        String address,

        @Schema(description = "난방방식", example = "개별난방")
        String heating,

        @Schema(description = "총세대수", example = "500")
        Integer totalHouseholds,

        @Schema(description = "공급호수합계", example = "50")
        Integer totalSupplyInNotice,

        @Schema(description = "1KM 이내 주요 생활편의 시설")
        List<FacilityType> infra,

        @Schema(description = "유닛타입 개수", example = "3")
        Integer unitCount,

        @Schema(description = "유닛타입 코드 목록", example = "[\"59A\", \"59B\", \"84A\"]")
        List<String> unitTypes,

        @Schema(description = "대중교통 총 소요 시간 (공고 상세조회용)", example = "1시간 30분")
        String totalTime,

        @Schema(description = "전체 대중교통 정보 (임대주택 상세조회용)")
        TransitInfoResponse distance
) {

    /// 정적 팩토리 메서드 - 임대주택 상세조회용 (TransitInfoResponse 포함)
    public static ComplexDetailResponse from(ComplexDocument document, NoticeFacilityListResponse facilities, TransitInfoResponse transitInfo) {

        return ComplexDetailResponse.builder()
                .id(document.getId())
                .name(document.getName())
                .address(extractRegion(document.getAddress().getFull()))
                .heating(document.getHeating())
                .totalHouseholds(
                        document.getTotalHouseholds() == null || document.getTotalHouseholds().isEmpty()
                                ? 0
                                : Integer.parseInt(document.getTotalHouseholds())
                )
                .totalSupplyInNotice(document.getTotalSupplyInNotice())
                .infra(facilities.infra())
                .unitCount(document.getUnitTypes().size())
                .unitTypes(document.getUnitTypes().stream()
                        .map(UnitType::getTypeCode)
                        .toList())
                .totalTime(null)
                .distance(transitInfo)
                .build();
    }

    /// 정적 팩토리 메서드 - 공고 상세조회용 (거리 정보 없음)
    public static List<ComplexDetailResponse> from(
            List<ComplexDocument> documents,
            Map<String, NoticeFacilityListResponse> facilityListResponseMap
    ) {
        return documents.stream()
                .map(document -> {
                    NoticeFacilityListResponse facilities =
                            facilityListResponseMap.getOrDefault(document.getId(), NoticeFacilityListResponse.empty());

                    return ComplexDetailResponse.builder()
                            .id(document.getId())
                            .name(document.getName())
                            .address(extractRegion(document.getAddress().getFull()))
                            .heating(document.getHeating())
                            .totalHouseholds(null)
                            .totalSupplyInNotice(null)
                            .infra(facilities.infra())
                            .unitCount(document.getUnitTypes().size())
                            .unitTypes(null)
                            .totalTime(null)
                            .distance(null)
                            .build();
                })
                .toList();
    }

    /// 정적 팩토리 메서드 - 공고 상세조회용 (totalTime 포함)
    public static List<ComplexDetailResponse> from(
            List<ComplexDocument> documents,
            Map<String, NoticeFacilityListResponse> facilityListResponseMap,
            Map<String, Integer> totalTimeMap
    ) {
        return documents.stream()
                .map(document -> {
                    NoticeFacilityListResponse facilities =
                            facilityListResponseMap.getOrDefault(document.getId(), NoticeFacilityListResponse.empty());
                    Integer totalTimeMinutes =
                            totalTimeMap.getOrDefault(document.getId(), 0);

                    return ComplexDetailResponse.builder()
                            .id(document.getId())
                            .name(document.getName())
                            .address(extractRegion(document.getAddress().getFull()))
                            .heating(document.getHeating())
                            .totalHouseholds(null)
                            .totalSupplyInNotice(null)
                            .infra(facilities.infra())
                            .unitCount(document.getUnitTypes().size())
                            .unitTypes(null)
                            .totalTime(TimeFormatter.formatTime(totalTimeMinutes))
                            .distance(null)
                            .build();
                })
                .toList();
    }

    /**
     * 전체 주소에서 시/도 + 시/군/구만 추출
     * @param fullAddress 전체 주소 (예: "서울특별시 서초구 서초동 123-45")
     * @return 시/도 + 시/군/구 (예: "서울특별시 서초구")
     */
    private static String extractRegion(String fullAddress) {
        if (fullAddress == null || fullAddress.isEmpty()) {
            return "";
        }

        String[] parts = fullAddress.trim().split("\\s+");

        // 최소 2개 부분이 있어야 함 (시/도 + 시/군/구)
        if (parts.length < 2) {
            return fullAddress;
        }

        return parts[0] + " " + parts[1];
    }

}
