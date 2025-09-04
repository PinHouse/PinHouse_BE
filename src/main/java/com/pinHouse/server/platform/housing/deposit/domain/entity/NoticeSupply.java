package com.pinHouse.server.platform.housing.deposit.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeSupply {

    @Field("공급유형")
    private String housingType;

    @Field("전용면적")
    private String area;

    @Field("월임대료(원)")
    private Integer monthlyRent;

    @Field("임대보증금(원)")
    private Deposit deposit;

    @Field("모집호수")
    private Recruitment recruitmentCount;
}
