package com.pinHouse.server.platform.housing.complex.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Deposit {

    @Field("total")
    private long total;

    @Field("contract")
    private long contract;

    @Field("interim")
    private long interim;

    @Field("balance")
    private long balance;

    /// 빌더 생성자
    @Builder
    public Deposit(long total, long contract, long interim, long balance) {
        this.total = total;
        this.contract = contract;
        this.interim = interim;
        this.balance = balance;
    }
}
