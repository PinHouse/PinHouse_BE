package com.pinHouse.server.platform.domain.diagnosis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Region {

    // 코드
    private String code;

    // 법정동명
    private String address;

    public static Region of(String code, String address) {
        return Region.builder()
                .code(code)
                .address(address)
                .build();
    }

}
