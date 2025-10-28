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

        @Schema(description = "타입별 인프라 개수",
                example = "{\"LIBRARY\":3,\"PARK\":5,\"HOSPITAL\":2}")
        Map<String, Integer> counts

) {
    /// 정적 팩토리 메서드
    public static NoticeFacilityListResponse from(Map<FacilityType, Integer> src) {

        /// 입력 받은 내용 파싱하기
        Map<String, Integer> converted = src.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().getValue(), Map.Entry::getValue));

        return NoticeFacilityListResponse.builder()
                .counts(converted)
                .build();
    }
}
