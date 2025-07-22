package com.myhome.server.platform.adapter.out.mongo.notice;

import com.myhome.server.platform.domain.notice.NoticeReceptionCenter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeReceptionCenterDocument {

    @Field("소재지")
    private String location;

    @Field("전화번호")
    private String phoneNumber;

    @Field("운영기간")
    private String operationPeriod;

    /// ToDomain
    NoticeReceptionCenter toDomain() {
        return NoticeReceptionCenter.builder()
                .location(location)
                .phoneNumber(phoneNumber)
                .operationPeriod(operationPeriod)
                .build();
    }
}
