package com.pinHouse.server.platform.notice.application.dto.response;

import com.pinHouse.server.platform.notice.domain.NoticeSupplyInfo;
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

    /**
     * [ 사용자 변환에 따른 임대 옵션 응답] DTO
     * - 임대 옵션 정보를 응답할 때 사용합니다.
     * - 추후, 해당 부분은 백엔드의 기능이 아닌, 프런트를 통해 이뤄질 수 있습니다
     *
     * @param noticeId                공고 아이디
     * @param deposit           변환된 보증금
     * @param rent              변환된 월세
     */
    @Builder
    public record NoticeLeaseOptionResponse(
            String noticeId,
            String housingType,
            long deposit,
            long rent
    ) {

        /// 정적 팩토리 메서드
        public static NoticeLeaseOptionResponse from(String noticeId, String housingType, long deposit, long rent) {

            return NoticeLeaseOptionResponse.builder()
                    .noticeId(noticeId)
                    .housingType(housingType)
                    .deposit(deposit)
                    .rent(rent)
                    .build();
        }

    }
}
