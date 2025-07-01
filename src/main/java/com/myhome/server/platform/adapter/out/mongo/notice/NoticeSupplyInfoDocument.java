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

    @Field("주택형")
    private String housingType;

    @Field("전용면적(㎡)")
    private String area;

    @Field("세대수")
    private String households;

    @Field("금회공급 세대수(예비자 포함)")
    private String supplyHouseholds;

    @Field("임대보증금(원)")
    private String deposit;

    @Field("월임대료(원)")
    private String monthlyRent;

    @Field("인터넷청약")
    private String internetSubscription;

    @Field("주택형안내")
    private String housingTypeInfo;

    /// ToDomain
    NoticeSupplyInfo toDomain() {
        return NoticeSupplyInfo.builder()
                .housingType(housingType)
                .area(area)
                .households(households)
                .supplyHouseholds(supplyHouseholds)
                .deposit(deposit)
                .monthlyRent(monthlyRent)
                .internetSubscription(internetSubscription)
                .housingTypeInfo(housingTypeInfo)
                .build();
    }

}
