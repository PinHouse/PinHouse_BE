package com.pinHouse.server.platform.housing.complex.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;


@Schema(name = "[응답][임대 옵션] 임대 옵션 정보 응답", description = "임대 옵션 정보를 응답할 때 사용되는 DTO입니다.")
@Builder
public record DepositResponse(

        DepositMinMaxResponse min,
        DepositMinMaxResponse normal,
        DepositMinMaxResponse max

) {

    /// 정적 팩토리 메서드
    public static DepositResponse from(DepositMinMaxResponse min, DepositMinMaxResponse normal,DepositMinMaxResponse max) {

        return DepositResponse.builder()
                .min(min)
                .normal(normal)
                .max(max)
                .build();
    }

}
