package com.pinHouse.server.platform.notice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class NoticeSupplyInfo {

    private String housingType;      // 공급유형
    private String area;             // 전용면적
    private Integer monthlyRent;     // 월임대료
    private Deposit deposit;         // 임대보증금 세부
    private RecruitmentCount recruitmentCount; // 모집호수

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Deposit {
        private Integer total;
        private Integer contract;
        private Integer middle;
        private Integer balance;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class RecruitmentCount {
        private Integer total;
        private Integer priority;
        private Integer general;
    }
}
