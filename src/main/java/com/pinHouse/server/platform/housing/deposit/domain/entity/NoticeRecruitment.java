package com.pinHouse.server.platform.housing.deposit.domain.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeRecruitment {

    @Field("계")
    private Integer total;

    @Field("우선공급")
    private Integer priority;

    @Field("일반공급")
    private Integer general;

    /// 빌더 생성자
    @Builder
    public NoticeRecruitment(Integer total, Integer priority, Integer general) {
        this.total = total;
        this.priority = priority;
        this.general = general;
    }
}
