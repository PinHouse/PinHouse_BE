package com.pinHouse.server.platform.housing.deposit.domain.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeSupply {

    @Field("공급유형")
    private String housingType;

    @Field("전용면적")
    private String area;

    @Field("월임대료(원)")
    private Integer monthlyRent;

    @Field("임대보증금(원)")
    private NoticeDeposit noticeDeposit;

    @Field("모집호수")
    private NoticeRecruitment noticeRecruitmentCount;

    /// 빌더 생성자
    @Builder
    public NoticeSupply(String housingType, String area, Integer monthlyRent, NoticeDeposit noticeDeposit, NoticeRecruitment noticeRecruitmentCount) {
        this.housingType = housingType;
        this.area = area;
        this.monthlyRent = monthlyRent;
        this.noticeDeposit = noticeDeposit;
        this.noticeRecruitmentCount = noticeRecruitmentCount;
    }
}
