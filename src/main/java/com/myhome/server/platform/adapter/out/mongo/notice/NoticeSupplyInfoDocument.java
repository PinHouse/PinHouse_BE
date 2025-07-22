package com.myhome.server.platform.adapter.out.mongo.notice;

import com.myhome.server.platform.domain.notice.NoticeSupplyInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeSupplyInfoDocument {

    @Field("공급유형")
    private String housingType;

    @Field("전용면적")
    private String area;

    @Field("월임대료(원)")
    private Integer monthlyRent;

    @Field("임대보증금(원)")
    private DepositDocument deposit;

    @Field("모집호수")
    private RecruitmentCountDocument recruitmentCount;

    public NoticeSupplyInfo toDomain() {
        return NoticeSupplyInfo.builder()
                .housingType(housingType)
                .area(area)
                .monthlyRent(monthlyRent)
                .deposit(deposit.toDomain())
                .recruitmentCount(recruitmentCount.toDomain())
                .build();
    }
}
