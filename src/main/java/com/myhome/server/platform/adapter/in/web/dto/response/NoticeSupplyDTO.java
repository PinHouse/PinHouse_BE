package com.myhome.server.platform.adapter.in.web.dto.response;

import com.myhome.server.platform.domain.notice.NoticeSupplyInfo;
import lombok.Builder;

public record NoticeSupplyDTO() {

    @Builder
    public record NoticeSupplyResponse(
            String housingType,
            String area,
            double deposit,
            double monthlyRent,
            Integer recruitment
    ) {
        /// 정적 팩토리 메서드
        public static NoticeSupplyResponse from(NoticeSupplyInfo notice) {
            return NoticeSupplyResponse.builder()
                    .housingType(notice.getHousingType())
                    .area(notice.getArea())
                    .deposit(notice.getDeposit().getTotal())
                    .monthlyRent(notice.getMonthlyRent())
                    .recruitment(notice.getRecruitmentCount().getTotal())
                    .build();
        }

    }
}
