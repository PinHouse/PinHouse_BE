package com.pinHouse.server.platform.housing.deposit.application.dto;

import com.pinHouse.server.platform.housing.deposit.domain.entity.NoticeSupply;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "[응답][공급] 공고 공급 정보 응답", description = "공고 공급 정보를 응답하는 DTO입니다.")
@Builder
public record NoticeSupplyResponse(
        @Schema(description = "주택 유형", example = "아파트")
        String housingType,

        @Schema(description = "면적", example = "84.5")
        String area,

        @Schema(description = "보증금", example = "50000000")
        double deposit,

        @Schema(description = "월세", example = "450000")
        double monthlyRent,

        @Schema(description = "모집 인원", example = "3")
        Integer recruitment
) {
    /// 정적 팩토리 메서드
    public static NoticeSupplyResponse from(NoticeSupply notice) {
        return NoticeSupplyResponse.builder()
                .housingType(notice.getHousingType())
                .area(notice.getArea())
                .deposit(notice.getNoticeDeposit().getTotal())
                .monthlyRent(notice.getMonthlyRent())
                .recruitment(notice.getNoticeRecruitmentCount().getTotal())
                .build();
    }

}
