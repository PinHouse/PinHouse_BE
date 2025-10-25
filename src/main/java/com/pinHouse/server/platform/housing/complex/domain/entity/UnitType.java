package com.pinHouse.server.platform.housing.complex.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UnitType {

    @Field("typeCode")
    private String typeCode;

    @Field("exclusiveAreaM2")
    private double exclusiveAreaM2;

    @Field("monthlyRent")
    private int monthlyRent;

    @Field("deposit")
    private Deposit deposit;

    @Field("quota")
    private Quota quota;

    /// 빌더 생성자
    @Builder
    public UnitType(String typeId, String typeCode, double exclusiveAreaM2, int monthlyRent, Deposit deposit, Quota quota) {
        this.typeCode = typeCode;
        this.exclusiveAreaM2 = exclusiveAreaM2;
        this.monthlyRent = monthlyRent;
        this.deposit = deposit;
        this.quota = quota;
    }
}
