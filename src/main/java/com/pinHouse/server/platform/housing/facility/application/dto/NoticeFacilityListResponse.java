package com.pinHouse.server.platform.housing.facility.application.dto;

import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;
import java.util.Map;

/**
 * 인프라 관련 응답 DTO 클래스입니다.
 */
@Builder
@Schema(name = "[응답][시설] 주변 인프라 시설 목록", description = "단지 주변 1KM 이내의 주요 생활편의 시설 정보")
public record NoticeFacilityListResponse(
        @Schema(description = "주변 인프라 시설 타입 목록 (3개 이상 있는 시설만 포함)", example = "[\"문화센터\", \"병원-약국\"]")
        List<FacilityType> infra
) {
    /// 정적 팩토리 메서드
    public static NoticeFacilityListResponse from(Map<FacilityType, Integer> src) {
        if (src == null || src.isEmpty()) {
            return NoticeFacilityListResponse.empty();
        }

        List<FacilityType> infraList = src.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue() >= 3)
                .map(entry -> entry.getKey().displayType())
                .distinct()
                .toList();

        return NoticeFacilityListResponse.builder()
                .infra(infraList)
                .build();
    }

    /// 빈 응답용
    public static NoticeFacilityListResponse empty() {
        return NoticeFacilityListResponse.builder()
                .infra(List.of())
                .build();
    }
}
