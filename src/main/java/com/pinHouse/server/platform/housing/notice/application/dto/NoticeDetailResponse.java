package com.pinHouse.server.platform.housing.notice.application.dto;

import com.pinHouse.server.platform.housing.deposit.application.dto.NoticeSupplyResponse;
import com.pinHouse.server.platform.housing.notice.domain.entity.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(name = "[응답][공고] 공고 상세 조회 응답", description = "공고 상세 조회를 위한 DTO입니다.")
@Builder
public record NoticeDetailResponse(
        @Schema(description = "공고ID", example = "101")
        String noticeId,

        @Schema(description = "공고명", example = "2025년 하반기 주택 공급 공고")
        String noticeName,

        @Schema(description = "공급기관명", example = "국토교통부")
        String supplier,

        @Schema(description = "모집시기", example = "2025년 10월 ~ 11월")
        String applicationPeriod,

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
        String expectedMoveInDate,

        @Schema(description = "공급정보 목록")
        List<NoticeSupplyResponse> supply
) {

    /// 정적 팩토리 메서드입니다.
    public static NoticeDetailResponse from(Notice notice) {
        return NoticeDetailResponse.builder()
                .noticeId(notice.getId())
                .noticeName(notice.getTitle())
                .supplier(notice.getSupplier())
                .applicationPeriod(notice.getStartDate())
                .complexName(notice.getComplexName())
                .address(notice.getAddress())
                .latitude(notice.getLocation().getLatitude())
                .longitude(notice.getLocation().getLongitude())
                .householdCount(notice.getTotalHouseholds())
                .heatingType(notice.getHeatingMethod())
                .expectedMoveInDate(notice.getEndDate())
                .supply(notice.getSupplyInfo().stream()
                        .map(NoticeSupplyResponse::from)
                        .toList())
                .build();

    }

}
