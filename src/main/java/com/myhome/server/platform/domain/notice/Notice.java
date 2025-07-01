package com.myhome.server.platform.domain.notice;

import com.myhome.server.platform.domain.location.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notice {

    private String id;

    private String noticeId;

    private String complexName;

    private String status;

    private String startDate;

    private String type;

    private String title;

    private String views;

    private String endDate;

    private String address;

    private String region;

    private Location location;

    private NoticeComplex complex;

    private List<NoticeSupplyInfo> supplyInfo;

    private NoticeReceptionCenter receptionCenter;

    private NoticeSchedule noticeSchedule;

    private List<Map<String, Object>> leaseConditions;

}

