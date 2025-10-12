package com.pinHouse.server.platform.housing.complex.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;


@Schema(name = "[응답][임대 옵션] 임대 옵션 정보 응답", description = "임대 옵션 정보를 응답할 때 사용되는 DTO입니다.")
@Builder
public record DepositResponse(
        @Schema(description = "임대주택 아이디", example = "N20241011-001")
        String complexId,

        @Schema(description = "주택 유형", example = "26A")
        String housingType,

        @Schema(description = "계약금", example = "50000000")
        long contract,

        @Schema(description = "잔금", example = "50000000")
        long balance,

        @Schema(description = "월 임대료", example = "500000")
        long monthPay
) {

    /// 정적 팩토리 메서드
    public static DepositResponse from(String complexId, String housingType, long contract, long balance, long monthPay) {

        return DepositResponse.builder()
                .complexId(complexId)
                .housingType(housingType)
                .contract(contract)
                .balance(balance)
                .monthPay(monthPay)
                .build();
    }

}
