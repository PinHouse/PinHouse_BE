package com.myhome.server.platform.domain.notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeSupplyInfo {

    private String housingType;

    private String area;

    private String households;

    private String supplyHouseholds;

    private String deposit;

    private String monthlyRent;

    private String internetSubscription;

    private String housingTypeInfo;


}
