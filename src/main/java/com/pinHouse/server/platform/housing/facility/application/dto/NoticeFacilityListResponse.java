package com.pinHouse.server.platform.housing.facility.application.dto;

import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.housing.facility.domain.entity.infra.*;
import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 인프라 관련 응답 DTO 클래스입니다.
 */
@Builder
public record NoticeFacilityListResponse(
        List<FacilityType> infra
) {
    /// 정적 팩토리 메서드
    public static NoticeFacilityListResponse from(Map<FacilityType, Integer> src) {
        if (src == null || src.isEmpty()) {
            return NoticeFacilityListResponse.empty();
        }

        List<FacilityType> infraList = src.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue() >= 3)
                .map(Map.Entry::getKey)
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
