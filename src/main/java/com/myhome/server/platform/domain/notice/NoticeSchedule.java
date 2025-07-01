package com.myhome.server.platform.domain.notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeSchedule{

    private String receptionPeriod;

    private String documentReceptionPeriod;

    private String documentTargetAnnouncementDate;

    private String winnerAnnouncementDate;

    private String contractPeriod;

}
