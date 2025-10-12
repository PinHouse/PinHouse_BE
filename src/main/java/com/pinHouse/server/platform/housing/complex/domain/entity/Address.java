package com.pinHouse.server.platform.housing.complex.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    @Field("full")
    private String full;

    @Field("road")
    private String road;

    /// 빌더 생성자
    @Builder
    public Address(String full, String road) {
        this.full = full;
        this.road = road;
    }
}
