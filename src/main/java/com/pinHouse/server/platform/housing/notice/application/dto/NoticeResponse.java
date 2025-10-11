package com.pinHouse.server.platform.housing.notice.application.dto;

import com.pinHouse.server.platform.housing.notice.domain.entity.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record NoticeResponse(

        @Schema(description = "단지 이름", example = "한강자이 아파트")
        String complexName,

        @Schema(description = "주소", example = "서울특별시 강남구 테헤란로 123")
        String address,

        @Schema(description = "위도", example = "37.4979")
        Double latitude,

        @Schema(description = "경도", example = "127.0276")
        Double longitude,

        @Schema(description = "총세대수", example = "1500세대")
        String householdCount,

        @Schema(description = "난방방식", example = "중앙난방")
        String heatingType,

        @Schema(description = "당첨자발표일자", example = "2025-12-01")
        String expectedMoveInDate
) {
    /// 정적 팩토리 메서드입니다.
    public static NoticeResponse from(Notice notice) {
        return NoticeResponse.builder()
                .complexName(notice.getComplexName())
                .address(notice.getAddress())
                .latitude(notice.getLocation().getLatitude())
                .longitude(notice.getLocation().getLongitude())
                .householdCount(notice.getTotalHouseholds())
                .heatingType(notice.getHeatingMethod())
                .expectedMoveInDate(notice.getEndDate())
                .build();

    }

}
