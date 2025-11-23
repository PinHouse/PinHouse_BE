package com.pinHouse.server.platform.housing.notice.application.dto;

import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.complex.domain.entity.Deposit;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.*;

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
     * 정적 팩토리 메서드 - 단지 목록으로부터 필터 정보 추출
     */
    public static ComplexFilterResponse from(List<ComplexDocument> complexes) {
        // 1. 지역 필터 정보 추출
        DistrictFilter districtFilter = DistrictFilter.from(complexes);

        // 2. 가격 필터 정보 추출
        CostFilter costFilter = CostFilter.from(complexes);

        // 3. 면적(타입코드) 필터 정보 추출
        AreaFilter areaFilter = AreaFilter.from(complexes);

        return ComplexFilterResponse.builder()
                .districtFilter(districtFilter)
                .costFilter(costFilter)
                .areaFilter(areaFilter)
                .build();
    }

    /**
     * 지역 필터 정보
     */
    @Builder
    @Schema(name = "지역 필터", description = "단지가 속한 지역(구) 목록")
    public record DistrictFilter(
            @Schema(description = "고유한 지역(구) 목록", example = "[\"성남시 분당구\", \"서울시 강남구\", \"부산시 해운대구\"]")
            List<String> districts
    ) {
        public static DistrictFilter from(List<ComplexDocument> complexes) {
            List<String> uniqueDistricts = complexes.stream()
                    .map(ComplexDocument::getCounty)
                    .filter(Objects::nonNull)
                    .filter(county -> !county.isBlank())
                    .distinct()
                    .sorted()
                    .toList();

            return DistrictFilter.builder()
                    .districts(uniqueDistricts)
                    .build();
        }
    }

    /**
     * 가격 필터 정보
     */
    @Builder
    @Schema(name = "가격 필터", description = "단지의 가격 범위 및 분포 정보")
    public record CostFilter(
            @Schema(description = "최소 가격 (보증금)", example = "5000000")
            long minPrice,

            @Schema(description = "최대 가격 (보증금)", example = "150000000")
            long maxPrice,

            @Schema(description = "평균 가격 (보증금)", example = "45000000")
            long avgPrice,

            @Schema(description = "가격 분포 (최대 20개 구간)")
            List<PriceDistribution> priceDistribution
    ) {
        public static CostFilter from(List<ComplexDocument> complexes) {
            // 모든 unitType의 보증금(deposit.total) 수집
            List<Long> allPrices = complexes.stream()
                    .flatMap(complex -> complex.getUnitTypes().stream())
                    .map(UnitType::getDeposit)
                    .filter(Objects::nonNull)
                    .map(Deposit::getTotal)
                    .filter(price -> price > 0)
                    .toList();

            if (allPrices.isEmpty()) {
                return CostFilter.builder()
                        .minPrice(0)
                        .maxPrice(0)
                        .avgPrice(0)
                        .priceDistribution(List.of())
                        .build();
            }

            // 통계 계산
            long minPrice = allPrices.stream().min(Long::compareTo).orElse(0L);
            long maxPrice = allPrices.stream().max(Long::compareTo).orElse(0L);
            long avgPrice = (long) allPrices.stream().mapToLong(Long::longValue).average().orElse(0.0);

            // 가격 분포 계산 (최대 20개 구간)
            List<PriceDistribution> distribution = calculatePriceDistribution(allPrices, minPrice, maxPrice);

            return CostFilter.builder()
                    .minPrice(minPrice)
                    .maxPrice(maxPrice)
                    .avgPrice(avgPrice)
                    .priceDistribution(distribution)
                    .build();
        }

        /**
         * 가격 분포 계산 (최대 20개 구간으로 나누어 각 구간의 개수 계산)
         */
        private static List<PriceDistribution> calculatePriceDistribution(
                List<Long> prices,
                long minPrice,
                long maxPrice
        ) {
            if (prices.isEmpty() || minPrice == maxPrice) {
                return List.of(PriceDistribution.builder()
                        .rangeStart(minPrice)
                        .rangeEnd(maxPrice)
                        .count(prices.size())
                        .build());
            }

            // 구간 개수 결정 (최대 20개)
            int bucketCount = Math.min(20, prices.size());
            long range = maxPrice - minPrice;
            long bucketSize = Math.max(1, range / bucketCount);

            // 각 구간별 카운트 맵 생성
            Map<Integer, Long> bucketCounts = new HashMap<>();
            for (long price : prices) {
                int bucketIndex = (int) ((price - minPrice) / bucketSize);
                // 최대값이 정확히 maxPrice인 경우 마지막 버킷에 포함
                if (bucketIndex >= bucketCount) {
                    bucketIndex = bucketCount - 1;
                }
                bucketCounts.merge(bucketIndex, 1L, Long::sum);
            }

            // PriceDistribution 리스트 생성
            List<PriceDistribution> distributions = new ArrayList<>();
            for (int i = 0; i < bucketCount; i++) {
                long rangeStart = minPrice + (i * bucketSize);
                long rangeEnd = (i == bucketCount - 1) ? maxPrice : rangeStart + bucketSize - 1;
                long count = bucketCounts.getOrDefault(i, 0L);

                distributions.add(PriceDistribution.builder()
                        .rangeStart(rangeStart)
                        .rangeEnd(rangeEnd)
                        .count(count)
                        .build());
            }

            return distributions;
        }
    }

    /**
     * 가격 분포 구간
     */
    @Builder
    @Schema(name = "가격 분포 구간", description = "특정 가격 범위에 속하는 유닛 개수")
    public record PriceDistribution(
            @Schema(description = "구간 시작 가격", example = "10000000")
            long rangeStart,

            @Schema(description = "구간 종료 가격", example = "20000000")
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
    ) {
        public static AreaFilter from(List<ComplexDocument> complexes) {
            List<String> uniqueTypeCodes = complexes.stream()
                    .flatMap(complex -> complex.getUnitTypes().stream())
                    .map(UnitType::getTypeCode)
                    .filter(Objects::nonNull)
                    .filter(code -> !code.isBlank())
                    .distinct()
                    .sorted()
                    .toList();

            return AreaFilter.builder()
                    .typeCodes(uniqueTypeCodes)
                    .build();
        }
    }
}
