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
public class RecruitmentCountDocument {

    @Field("계")
    private Integer total;

    @Field("우선공급")
    private Integer priority;

    @Field("일반공급")
    private Integer general;

    public NoticeSupplyInfo.RecruitmentCount toDomain() {
        return NoticeSupplyInfo.RecruitmentCount.builder()
                .total(total)
                .priority(priority)
                .general(general)
                .build();
    }
}

