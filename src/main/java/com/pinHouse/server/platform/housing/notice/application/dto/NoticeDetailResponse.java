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
        String id,

        @Schema(description = "공고명", example = "2025년 하반기 주택 공급 공고")
        String name,

        @Schema(description = "공급주체", example = "LH")
        String supplier,

        @Schema(description = "공급유형", example = "영구임대")
        String type,

        @Schema(description = "모집시기", example = "2025년 10월 ~ 11월")
        String period,

        @Schema(description = "공급정보 목록")
        List<NoticeSupplyResponse> supply
) {

    /// 정적 팩토리 메서드입니다.
    public static NoticeDetailResponse from(Notice notice) {
        return NoticeDetailResponse.builder()
                .id(notice.getId())
                .name(notice.getTitle())
                .supplier(notice.getSupplier())
                .period(notice.getStartDate())
                .supply(notice.getSupplyInfo().stream()
                        .map(NoticeSupplyResponse::from)
                        .toList())
                .build();

    }

}
