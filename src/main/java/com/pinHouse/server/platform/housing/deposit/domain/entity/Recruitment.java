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
public class Recruitment {

    @Field("계")
    private Integer total;

    @Field("우선공급")
    private Integer priority;

    @Field("일반공급")
    private Integer general;
}

