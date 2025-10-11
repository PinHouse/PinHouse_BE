package com.pinHouse.server.platform.housing.deposit.domain.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeDeposit {

    @Field("계")
    private Integer total;

    @Field("계약금")
    private Integer contract;

    @Field("중도금")
    private Integer middle;

    @Field("잔금")
    private Integer balance;

    /// 빌더 생성자
    @Builder
    public NoticeDeposit(Integer total, Integer contract, Integer middle, Integer balance) {
        this.total = total;
        this.contract = contract;
        this.middle = middle;
        this.balance = balance;
    }
}
