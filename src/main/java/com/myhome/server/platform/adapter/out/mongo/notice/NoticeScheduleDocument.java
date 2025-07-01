package com.myhome.server.platform.adapter.out.mongo.notice;

import com.myhome.server.platform.domain.notice.NoticeSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeScheduleDocument {

    @Field("접수기간")
    private String receptionPeriod;

    @Field("서류접수기간")
    private String documentReceptionPeriod;

    @Field("서류제출대상자발표일")
    private String documentTargetAnnouncementDate;

    @Field("당첨자발표일")
    private String winnerAnnouncementDate;

    @Field("계약기간")
    private String contractPeriod;

    /// ToDomain
    NoticeSchedule toDomain(){
        return NoticeSchedule.builder()
                .receptionPeriod(receptionPeriod)
                .documentReceptionPeriod(documentReceptionPeriod)
                .documentTargetAnnouncementDate(documentTargetAnnouncementDate)
                .winnerAnnouncementDate(winnerAnnouncementDate)
                .contractPeriod(contractPeriod)
                .build();
    }

}
