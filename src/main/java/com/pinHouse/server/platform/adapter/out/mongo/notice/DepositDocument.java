package com.pinHouse.server.platform.adapter.out.mongo.notice;

import com.pinHouse.server.platform.domain.notice.NoticeSupplyInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositDocument {

    @Field("계")
    private Integer total;

    @Field("계약금")
    private Integer contract;

    @Field("중도금")
    private Integer middle;

    @Field("잔금")
    private Integer balance;

    public NoticeSupplyInfo.Deposit toDomain() {
        return NoticeSupplyInfo.Deposit.builder()
                .total(total)
                .contract(contract)
                .middle(middle)
                .balance(balance)
                .build();
    }
}
