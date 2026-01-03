package com.pinHouse.server.platform.housing.notice.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

/**
 * 공고의 단지 필터링 정보 응답
 * 프론트엔드에서 필터 UI를 구성하는데 필요한 데이터 제공
 */
@Builder
@Schema(name = "[응답][공고] 단지 필터링 정보", description = "공고에 포함된 단지들의 지역, 가격, 면적 필터링 정보")
public record ComplexFilterResponse(
        @Schema(description = "지역 필터 정보")
        DistrictFilter districtFilter,

        @Schema(description = "가격 필터 정보")
        CostFilter costFilter,

        @Schema(description = "면적(타입코드) 필터 정보")
        AreaFilter areaFilter
) {

    /**
     * 지역 필터 정보
     */
    @Builder
    @Schema(name = "지역 필터", description = "단지가 속한 지역(구) 목록")
    public record DistrictFilter(
            @Schema(description = "고유한 지역 목록")
            List<District> districts
    ) {}

    /**
     * 지역 정보 (city별로 그룹화된 districts)
     */
    @Builder
    @Schema(name = "지역 정보", description = "city별로 그룹화된 district 목록")
    public record District(
            @Schema(description = "시/도", example = "경기")
            String city,

            @Schema(description = "구/시 목록", example = "[\"동두천시\", \"양주시\"]")
            List<String> districts
    ) {}

    /**
     * 가격 필터 정보
     */
    @Builder
    @Schema(name = "가격 필터", description = "단지의 가격 범위 및 분포 정보")
    public record CostFilter(
            @Schema(description = "최소 가격 (보증금, 만원 단위)", example = "500")
            long minPrice,

            @Schema(description = "최대 가격 (보증금, 만원 단위)", example = "15000")
            long maxPrice,

            @Schema(description = "평균 가격 (보증금, 만원 단위)", example = "4500")
            long avgPrice,

            @Schema(description = "가격 분포 (최대 20개 구간)")
            List<PriceDistribution> priceDistribution
    ) {}

    /**
     * 가격 분포 구간
     */
    @Builder
    @Schema(name = "가격 분포 구간", description = "특정 가격 범위에 속하는 유닛 개수")
    public record PriceDistribution(
            @Schema(description = "구간 시작 가격 (만원 단위)", example = "1000")
            long rangeStart,

            @Schema(description = "구간 종료 가격 (만원 단위)", example = "2000")
            long rangeEnd,

            @Schema(description = "해당 구간에 속하는 유닛 개수", example = "45")
            long count
    ) {}

    /**
     * 면적(타입코드) 필터 정보
     */
    @Builder
    @Schema(name = "면적 필터", description = "단지의 고유 타입코드 목록")
    public record AreaFilter(
            @Schema(description = "고유한 타입코드 목록", example = "[\"84A\", \"59B\", \"101C\"]")
            List<String> typeCodes
    ) {}
}
