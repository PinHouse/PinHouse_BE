package com.pinHouse.server.platform.domain.notice;

import com.pinHouse.server.platform.domain.location.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class Notice {

    private String id;
    private String noticeId;
    private String complexName;
    private String status;
    private String startDate;
    private String supplier;
    private String type;
    private String title;
    private String views;
    private String endDate;
    private String address;
    private String region;
    private Location location;

    private List<NoticeSupplyInfo> supplyInfo;

    private String noticeUrl;
    private String myHomePcUrl;
    private String myHomeMobileUrl;
    private String contact;

    private String winnerAnnouncementDate;
    private String heatingMethod;
    private String totalHouseholds;
}


